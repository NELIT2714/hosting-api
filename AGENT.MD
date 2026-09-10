# Vizora Host API — Техническое описание бэкенда

---

## 1. Архитектура

### Общая архитектура

**Модульный монолит** с реактивной моделью выполнения. Все домены (пользователи, виртуальные машины, платежи, ноды, промокоды и т.д.) находятся в одном Spring Boot приложении с чёткой слоистой структурой. Внешняя инфраструктура (VM Manager) вынесена за gRPC-границу.

### Технологический стек

| Компонент | Технология | Версия |
|---|---|---|
| Язык | Java | 25 |
| Фреймворк | Spring Boot | 4.0.6 |
| Web-слой | Spring WebFlux | — |
| БД-доступ | Spring Data R2DBC | — |
| БД | PostgreSQL | — |
| Кэш | Redis (Reactive) | — |
| Аутентификация | JJWT | 0.13.0 |
| Межсервисное взаимодействие | gRPC | 1.84.0 |
| Protobuf | Protobuf | 4.36.1 |
| Платёжная система | Stripe Java SDK | 33.4.1 |
| Маппинг | MapStruct | 1.6.3 |
| Кодогенерация | Lombok | — |
| Документация API | SpringDoc OpenAPI | 3.1.0 |
| Криптография | BouncyCastle | 1.85 |
| JSON (вспомогательно) | Gson | 2.14.0 |
| Сборка | Maven | (wrapper) |

### Стиль работы с данными

**Полностью реактивный** (`Project Reactor` — `Mono<T>` / `Flux<T>`). Все репозитории, сервисы и контроллеры работают в реактивном стиле. Исключение — gRPC-клиент (`VMManagerBlockingStub`), который использует синхронный блокирующий вызов.

### Базы данных

- **PostgreSQL** — основное хранилище. Все доменные сущности. Доступ через R2DBC (реактивный JDBC-аналог).
- **Redis** — используется реактивный клиент. Конкретные данные (сессии, кэш, rate limiting) — конфигурация есть, детали использования в коде зависят от сервисов.

### Взаимодействие между компонентами

- **REST** — все внешние клиентские API (`/v1/...`)
- **gRPC** — коммуникация с VM Manager (управление жизненным циклом виртуальных машин)
- **Stripe Webhooks** — входящие события от платёжного шлюза (`POST /v1/webhooks/stripe`)
- **Scheduled Tasks** — внутренние фоновые задачи через Spring Scheduling

### Структура проекта (пакеты)

```
dev.nelit.api/
├── config/              # Spring/gRPC/OpenAPI/Stripe конфигурации, PKI-пропертисы
├── controller/          # REST-контроллеры (10 штук)
├── domain/
│   ├── entity/          # JPA/R2DBC сущности (таблицы БД)
│   ├── exception/       # Иерархия кастомных исключений
│   ├── dto/
│   │   ├── request/     # DTO для входящих запросов
│   │   └── response/    # DTO для исходящих ответов
│   ├── enums/           # Перечисления (статусы, типы)
│   └── mapper/          # MapStruct-маппинги
├── repository/          # Spring Data R2DBC репозитории
├── service/
│   ├── (interfaces)/    # Контракты сервисов
│   └── impl/            # Реализации сервисов
├── security/            # JWT-фильтр, SecurityConfig
├── scheduler/           # Фоновые задачи
├── util/                # Вспомогательные классы (TokenHasher, PemUtils и др.)
├── grpc/                # gRPC-клиент (VmManagerClient)
└── proto/               # .proto-файлы (vm_manager.proto)
```

Слоистая архитектура: `Controller → Service (interface/impl) → Repository → DB`. Дополнительный слой обработчиков (`CheckoutDetailsHandler`, `PaymentFulfillmentHandler`) для стратегий оформления заказов.

---

## 2. Функциональность по доменам

---

### 2.1 Аутентификация и сессии

**Сущности:** `users`, `sessions`

**Session:** `id_session`, `id_user`, `refresh_token_hash`, `created_at`, `expires_at`, `revoked_at`

**API** (`/v1/auth`):

| Метод | Путь | Описание |
|---|---|---|
| POST | `/v1/auth/login` | Аутентификация, установка JWT-кук (`accessToken`, `refreshToken`) |
| POST | `/v1/auth/refresh` | Обновление access-токена по refresh-токену |
| DELETE | `/v1/auth/logout` | Отзыв сессии |

**Бизнес-правила:**
- Refresh-токен хранится в БД в виде хэша (`TokenHasher`)
- Сессии имеют срок действия (`expires_at`) и могут быть отозваны (`revoked_at`)
- Токены передаются через HTTP-куки (в dev-режиме `Secure: false`)
- Refresh-токен ротируется при каждом обновлении

**Классы:** `AuthController`, `AuthServiceImpl`, `SessionServiceImpl`, `JwtServiceImpl`, `JwtAuthFilter`, `CookieServiceImpl`

---

### 2.2 Пользователи

**Сущности:** `users`, `users_telegram`

**User:** `id_user`, `email`, `password_hash`, `deleted_at`, `created_at`
**UserTelegram:** `telegram_id`, `first_name`, `last_name`, `username`

**API** (`/v1/users`):

| Метод | Путь | Описание |
|---|---|---|
| POST | `/v1/users` | Регистрация нового пользователя |
| PATCH | `/v1/users/change-password` | Смена пароля (требует аутентификации) |

**DTO запросов:**
- `Register`: email, password, passwordConfirmation
- `ChangePassword`: currentPassword, newPassword, passwordConfirmation

**Бизнес-правила:**
- Уникальность email (`EmailAlreadyExistsException` 409)
- Подтверждение пароля при регистрации (`PasswordsDontMatchException` 400)
- Проверка текущего пароля при смене (`CurrentPasswordIncorrectException` 400)
- Мягкое удаление (`deleted_at`) — поле есть в схеме, восстановление через API не реализовано

**Классы:** `UserController`, `UserServiceImpl`, `UserRepository`, `UserMapper`

---

### 2.3 Администраторы и права доступа

**Сущности:** `admins`, `admin_permissions`

**Admin:** `id_admin`, `id_user`, `created_at`
**AdminPermission:** `id_admin`, `permission` (enum)

**API** (`/v1/admins`):

| Метод | Путь | Описание |
|---|---|---|
| POST | `/v1/admins` | Назначить пользователя администратором |
| GET | `/v1/admins` | Список администраторов |

**Бизнес-правила:**
- Назначение только существующих пользователей (`UserIsNotAdminException` 400)
- Хранение набора прав через отдельную таблицу `admin_permissions`
- `AdminPermission` — enum, конкретные значения определяются в коде

**Классы:** `AdminController`, `AdminServiceImpl`, `AdminRepository`, `AdminPermissionRepository`

---

### 2.4 Ноды (инфраструктурные узлы)

**Сущности:** `nodes`

**Node:** `id_node`, `node_name`, `ip_address`, `grpc_port`, `location`, `cert_fingerprint`, `revoke_at`, `revoked_at`, `created_at`

**API** (`/v1/nodes`):

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| POST | `/v1/nodes/register` | Регистрация ноды (по enrollment-токену) | Нода |
| GET | `/v1/nodes` | Список нод | Admin |
| GET | `/v1/nodes/{node_id}` | Детали ноды | Admin |
| POST | `/v1/nodes` | Создание ноды (генерация токена) | Admin |
| PATCH | `/v1/nodes/{node_id}` | Обновление ноды | Admin |

**Бизнес-правила:**
- Регистрация ноды — двухэтапный процесс: сначала создаётся нода и генерируется enrollment-токен (`EnrollmentTokenService`), затем нода регистрируется по токену
- Уникальность имени ноды (`NodeNameAlreadyTakenException` 409)
- PKI: при регистрации нода получает сертификат, подписанный собственным CA (`CertificateAuthority`, `PemUtils`, BouncyCastle)
- Хранение отпечатка сертификата (`cert_fingerprint`) для верификации gRPC-соединения
- Поддержка отзыва сертификата (`revoke_at`, `revoked_at`)
- `nodeCertValidityDays` — настраиваемый срок действия сертификата

**Классы:** `NodeController`, `NodeServiceImpl`, `EnrollmentTokenServiceImpl`, `CertificateAuthority`, `PemUtils`, `NodeRepository`

---

### 2.5 Планы тарификации

**Сущности:** `plans`

**Plan:** `id_plan`, `plan_name`, `ram_mb`, `vcpus`, `disk_gb`, `price_per_month`

**API** (`/v1/plans`):

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| GET | `/v1/plans` | Список тарифных планов | Публичный |
| POST | `/v1/plans` | Создать план | Admin |
| PATCH | `/v1/plans/{plan_id}` | Обновить план | Admin |

**Бизнес-правила:**
- Уникальность имени плана (`PlanNameAlreadyTakenException` 409)

**Классы:** `PlanController`, `PlanServiceImpl`, `PlanRepository`, `PlanMapper`

---

### 2.6 OS-образы

**Сущности:** `os_images`

**OsImage:** `id_os_image`, `image_name`, `file_name`

**API** (`/v1/os-images`):

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| GET | `/v1/os-images` | Список доступных образов | Публичный |
| POST | `/v1/os-images` | Добавить образ | Admin |

**Бизнес-правила:**
- Уникальность образа (`OsImageAlreadyExists` 409)

**Классы:** `OsImageController`, `OsImageServiceImpl`, `OsImageRepository`, `OsImageMapper`

---

### 2.7 IP-пул

**Сущности:** `ip_pool`

**IpPool:** `id_ip`, `id_node`, `id_vm` (nullable), `ip_address`

**API** (`/v1/ip-pool`):

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| GET | `/v1/ip-pool` | Список IP-адресов | Admin |
| POST | `/v1/ip-pool` | Добавить IP-адрес | Admin |

**Бизнес-правила:**
- Уникальность IP-адреса (`IpAddressAlreadyExistsException` 409)
- Привязка IP к ноде и к конкретной VM
- При невозможности выделить IP — `NoAvailableAddressesException` 400
- Попытка назначить уже занятый IP — `IpAddressAlreadyAssignedException` 409

**Классы:** `IpPoolController`, `IpPoolServiceImpl`, `IpPoolRepository`, `IpPoolMapper`

---

### 2.8 Виртуальные машины

**Сущности:** `virtual_machines`, `vm_lifecycle`

**Vm:** `id_vm`, `id_user`, `id_node`, `id_plan`, `vm_name`, `uuid`, `is_active`, `is_blocked`, `expires_at`
**VmLifecycle:** `id_lifecycle`, `id_vm`, `blocked_at`, `delete_at`

**API** (`/v1/vms`):

| Метод | Путь | Описание |
|---|---|---|
| GET | `/v1/vms` | Список VM пользователя |
| POST | `/v1/vms/{vm_id}/activate` | Активировать VM (после оплаты) |
| POST | `/v1/vms/{vm_id}/reinstall` | Переустановить ОС |
| GET | `/v1/vms/{vm_id}/status` | Статус VM (через gRPC) |
| POST | `/v1/vms/{vm_id}/start` | Запустить VM (через gRPC) |
| POST | `/v1/vms/{vm_id}/stop` | Остановить VM (через gRPC) |
| POST | `/v1/vms/{vm_id}/restart` | Перезапустить VM (через gRPC) |
| POST | `/v1/vms/{vm_id}/console` | Получить VNC-токен консоли (через gRPC) |

**Бизнес-правила:**
- `is_active` — флаг активности, `is_blocked` — заблокированная VM
- `expires_at` — срок действия VM (задаётся при покупке или продлении)
- Активация требует предварительного создания заказа и оплаты
- Операции управления VM (start/stop/restart/console) проксируются через gRPC в VM Manager
- При активации проверяется наличие пароля или SSH-ключа (`PasswordOrSSHRequired` 400)
- Уникальное имя VM генерируется автоматически (`VMNameGenerator`)
- `VmLifecycle` хранит расписание блокировки и удаления (управляется планировщиком)

**Классы:** `VMController`, `VmServiceImpl`, `VmLifecycleServiceImpl`, `VmManagerClient`, `VmRepository`, `VmLifecycleRepository`, `VMMapper`

---

### 2.9 Платежи и оформление заказов

**Сущности:** `payments`, `receipts`, `vps_orders`, `vps_renewal_orders`

**Payment:** `id_payment`, `id_user`, `gateway`, `gatewayPaymentId`, `amount`, `currency`, `status` (PENDING/COMPLETED/FAILED/CANCELLED), `type` (VPS_PURCHASE/VPS_RENEWAL), `created_at`
**Receipt:** `id_receipt`, `id_payment`, `number`, buyer info, service description, `issued_at`
**VpsOrder:** `id_vps_order`, `id_vm`, `id_payment`, `id_plan`, `id_os_image`
**VpsRenewalOrder:** `id_renewal_order`, `id_vm`, `id_payment`, `days`

**API** (`/v1/checkout`):

| Метод | Путь | Описание |
|---|---|---|
| POST | `/v1/checkout` | Создать Checkout-сессию (Stripe) |

**Webhook** (`/v1/webhooks/stripe`):

| Метод | Путь | Описание |
|---|---|---|
| POST | `/v1/webhooks/stripe` | Обработка событий от Stripe |

**Архитектура платёжного флоу:**

```
CheckoutController
  → CheckoutServiceImpl
    → CheckoutDetailsHandler (VpsCheckoutHandler / VpsRenewalCheckoutHandler)
      → создание Payment (PENDING)
      → создание VpsOrder / VpsRenewalOrder
    → PaymentGatewayStrategy (StripeGatewayStrategy)
      → создание Stripe Checkout Session
      → возврат sessionId + checkoutUrl

StripeWebhookController
  → StripeWebhookServiceImpl
    → верификация подписи вебхука
    → PaymentCompletionServiceImpl
      → обновление Payment (COMPLETED/FAILED)
      → PaymentFulfillmentHandler (VpsPurchaseFulfillmentHandler / VpsRenewalFulfillmentHandler)
        → VPS_PURCHASE: создание VM, выделение IP, активация
        → VPS_RENEWAL: продление expires_at
      → ReceiptService (генерация чека)
```

**Бизнес-правила:**
- Тип платежа (`PaymentType`) определяет обработчик через стратегию
- Gateway-специфичные данные хранятся как JSONB в PostgreSQL
- `DuplicatePaymentException` 409 — защита от дублирования
- `CheckoutTypeNotSupportedException` 400 — неподдерживаемый тип заказа
- `GatewayNotImplemented` 400 — заглушка для будущих шлюзов
- Выполнение заказа (`fulfillment`) происходит только после подтверждения от Stripe
- `PaymentExpirationScheduler` — периодически отменяет просроченные PENDING-платежи

**Классы:** `CheckoutController`, `StripeWebhookController`, `CheckoutServiceImpl`, `StripeCheckoutServiceImpl`, `StripeGatewayStrategy`, `StripeWebhookServiceImpl`, `PaymentCompletionServiceImpl`, `VpsCheckoutHandler`, `VpsRenewalCheckoutHandler`, `VpsPurchaseFulfillmentHandler`, `VpsRenewalFulfillmentHandler`, `ReceiptService`, `PaymentRepository`, `ReceiptRepository`, `VpsOrderRepository`, `VpsRenewalOrderRepository`

---

### 2.10 Промокоды

**Сущности:** `promo_codes`, `promo_codes_uses`

**PromoCode:** `id_promo`, `code`, `amount_of_uses`, `discount`, `is_active`, `expires_at`, `created_at`
**PromoCodeUse:** `id_promo_use`, `id_user`, `id_promo`, `id_payment`, `status` (RESERVED/CONFIRMED/CANCELLED/EXPIRED), `expires_at`, `used_at`

**API** (`/v1/promo-codes`):

| Метод | Путь | Описание | Доступ |
|---|---|---|---|
| POST | `/v1/promo-codes` | Зарезервировать промокод | Пользователь |
| GET | `/v1/promo-codes` | Список промокодов | Admin |
| POST | `/v1/promo-codes` (admin) | Создать промокод | Admin |
| PATCH | `/v1/promo-codes/{promo_id}` | Обновить промокод | Admin |

**Бизнес-правила:**
- `amount_of_uses` — лимит использований. При исчерпании — `PromoCodeExhaustedException` 400
- Статус `RESERVED` — промокод забронирован при оформлении заказа, но платёж ещё не завершён
- Статус `CONFIRMED` — промокод применён после успешной оплаты
- Статус `EXPIRED` — бронь истекла без оплаты
- Один пользователь не может использовать один промокод дважды (`PromoCodeAlreadyUsedByUserException` 400)
- `PromoCodeCleanupScheduler` — периодически переводит просроченные RESERVED → EXPIRED
- `PromoCodeGenerator` — генерация случайного кода

**Классы:** `PromoCodeController`, `PromoCodeServiceImpl`, `PromoCodeUseServiceImpl`, `PromoCodeRepository`, `PromoCodeUseRepository`, `PromoCodeMapper`, `PromoCodeGenerator`, `PromoCodeCleanupScheduler`

---

## 3. Интеграции

### 3.1 Stripe (Платёжный шлюз)

- **SDK:** `stripe-java 33.4.1`
- **Протокол:** HTTPS (REST)
- **Используемые возможности:**
  - Checkout Sessions (hosted payment page)
  - Webhook-события (`payment_intent.succeeded`, `checkout.session.completed` и аналогичные)
  - Верификация подписи вебхука (`StripeProperties.webhookSecret`)
- **Конфигурация:** `StripeProperties`, `StripeConfig` (инициализация глобального `Stripe.apiKey`)

### 3.2 VM Manager (gRPC-сервис)

- **Протокол:** gRPC (TLS/mTLS)
- **Клиент:** `VMManagerBlockingStub` (синхронный)
- **Proto-файл:** `src/main/proto/vm_manager.proto`
- **Операции через gRPC:**
  - Создание VM
  - Активация VM
  - Переустановка ОС
  - Старт / Стоп / Рестарт
  - Получение статуса
  - Получение VNC-токена консоли
- **TLS:** `GrpcConfig` создаёт защищённый канал с использованием PKI-сертификатов из `PkiProperties`
- **Адрес:** `localhost:50052` (по умолчанию, из `application.yaml`)

### 3.3 Telegram (частичная интеграция)

- Сущность `users_telegram` (`telegram_id`, `first_name`, `last_name`, `username`) присутствует в схеме БД
- API-контроллеры и сервисы для работы с Telegram в коде не обнаружены — интеграция не завершена (только схема)

---

## 4. Безопасность

### Аутентификация

- **JWT** (JJWT 0.13.0)
- Access-токен + Refresh-токен в HTTP-куках
- `JwtAuthFilter` — WebFlux-фильтр, извлекает токен из `Authorization` заголовка (или куки), валидирует и устанавливает `SecurityContext`
- Refresh-токен хранится в БД в виде хэша (`TokenHasher` — предположительно SHA-256 или bcrypt-производная через BouncyCastle)
- При логауте — отзыв сессии в БД (`revoked_at`)

### Авторизация (RBAC)

- Разграничение на уровне `SecurityConfig`:
  - Публичные эндпоинты (регистрация, логин, список планов/образов)
  - Требующие аутентификации (управление VM, оформление заказов)
  - Admin-only (управление нодами, IP-пулом, планами, промокодами, пользователями)
- Права администратора хранятся в `admin_permissions` (гранулярный RBAC через enum)
- Проверка прав в `AdminServiceImpl`

### Чувствительные данные

- **Пароли:** хранятся как `password_hash` (bcrypt или аналог)
- **Refresh-токены:** хранятся как хэш (`refresh_token_hash`)
- **Stripe-ключи:** через `StripeProperties` из переменных окружения (`.env`)
- **JWT-секрет:** из переменных окружения
- **PKI-ключи:** хранятся вне репозитория (`C:/hosting/certs/`, путь в `application-dev.yml`), в `.gitignore` указана директория `certs/`
- **Платёжные данные Stripe:** не хранятся в БД напрямую — только `gatewayPaymentId` и статус
- **DB credentials:** через переменные окружения

### Webhook-безопасность

- Stripe webhook — верификация подписи через `webhookSecret`

---

## 5. Качество кода и инженерная зрелость

### Тесты

- Тестов в проекте **не обнаружено** — директория `src/test/` отсутствует или пуста.

### Документация API

- **OpenAPI/Swagger**: `SpringDoc OpenAPI 3.1.0`, Scalar UI (`/v3/api-docs`)
- Доступен в dev-профиле
- Серверы: `localhost:4000`, `api.vizora.host`
- JWT Bearer-схема описана в `OpenApiConfig`

### Обработка ошибок

- Иерархия кастомных исключений от абстрактного `DomainException` (code + HttpStatus)
- `GlobalExceptionHandler` (@RestControllerAdvice) — централизованный перехват `DomainException` и ошибок валидации
- Унифицированный формат ответа `ApiErrorResponse` (code, message, status)
- `include-message: always` в dev-профиле (`server.error`)

### Логирование

- Rolling-файловый аппендер: `logs/app.log`, ротация 10MB, хранение 30 дней, gzip-архивирование
- Наличие архивных логов: июнь–сентябрь 2026 (`logs/`)
- Debug-уровень для пакета `dev.nelit` в dev-профиле
- Мониторинг: Spring Actuator (`/actuator/health`, `/actuator/metrics`, `/actuator/loggers`)

### Паттерны и абстракции

| Паттерн | Где используется |
|---|---|
| Strategy | `PaymentGatewayStrategy` (Stripe, возможность добавить другие шлюзы) |
| Strategy | `CheckoutDetailsHandler` (VPS/Renewal) |
| Strategy | `PaymentFulfillmentHandler` (VPS Purchase/Renewal) |
| Repository | Spring Data R2DBC |
| DTO | Разделение Request/Response DTO |
| Mapper | MapStruct (отдельный маппер на каждый домен) |
| Service Layer | Interface + Implementation на каждый домен |
| Scheduler | Три планировщика для фоновых задач |
| Global Exception Handler | Единый `@RestControllerAdvice` |

### Технический долг и заметные особенности

- Telegram-интеграция: таблица есть, код не реализован
- `GatewayNotImplemented` — заглушка, означающая что в `CheckoutService` добавлен switch/when для будущих шлюзов, но только Stripe реализован
- gRPC-клиент — синхронный (`BlockingStub`) в реактивном приложении (потенциальное блокирование event loop)
- Отсутствие тестов

---

## 6. Полный список реализованных фич

**Аутентификация:**
- Регистрация по email + пароль
- Вход / выход
- Refresh-токен ротация
- Управление сессиями с отзывом

**Пользователи:**
- Регистрация с подтверждением пароля
- Смена пароля с проверкой текущего
- Мягкое удаление (поле в БД)

**Администрирование:**
- Назначение администраторов
- Гранулярные права через `admin_permissions`
- Просмотр списка администраторов

**Ноды:**
- Двухэтапная регистрация нод (токен → PKI-сертификат)
- Выпуск и хранение TLS-сертификатов для нод (собственный CA, BouncyCastle)
- Отзыв сертификатов
- CRUD нод с location-информацией

**Тарифные планы:**
- CRUD планов (ram, vcpu, disk, цена)
- Публичный список для клиентов

**OS-образы:**
- CRUD образов ОС
- Публичный список

**IP-пул:**
- Ручное управление пулом IP-адресов
- Привязка к ноде и VM
- Автоматическое выделение при создании VM

**Виртуальные машины:**
- Создание VM через gRPC после оплаты
- Активация, переустановка ОС
- Старт / Стоп / Рестарт через gRPC
- Получение статуса VM
- VNC-консоль (токен через gRPC)
- Автоматический срок действия (`expires_at`)
- Блокировка и расписание удаления (`vm_lifecycle`)
- Генерация уникального имени VM

**Платежи и заказы:**
- Stripe Checkout (hosted page)
- Покупка VPS (VPS_PURCHASE) — создание + активация VM после оплаты
- Продление VPS (VPS_RENEWAL) — продление срока действия после оплаты
- Генерация чеков (`receipts`)
- Обработка Stripe Webhook с верификацией подписи
- Автоматическая отмена просроченных PENDING-платежей (scheduler)
- Защита от дублирования платежей

**Промокоды:**
- Создание и управление промокодами (admin)
- Резервирование промокода при оформлении заказа
- Подтверждение/отмена при завершении платежа
- Лимит использований, срок действия, активация/деактивация
- Автоматическая очистка просроченных броней (scheduler)
- Генерация случайных кодов

**Фоновые задачи:**
- `PaymentExpirationScheduler` — отмена просроченных платежей
- `PromoCodeCleanupScheduler` — очистка просроченных броней промокодов
- `VmLifecycleScheduler` — управление жизненным циклом VM (блокировка, удаление)

**Инфраструктура:**
- PKI-подсистема (собственный CA для нод)
- gRPC-интеграция с VM Manager (TLS)
- Конфигурация через переменные окружения
- OpenAPI-документация (Scalar UI)
- Actuator endpoints (health, metrics, loggers)
- Ротирующие логи

---

## 7. Что отсутствует или выглядит незавершённым

| Область | Описание |
|---|---|
| **Telegram** | Таблицы `users_telegram` присутствуют в схеме, контроллеры/сервисы не реализованы |
| **Тесты** | Полное отсутствие unit/integration/e2e тестов |
| **Множественные платёжные шлюзы** | `PaymentGateway` enum и `GatewayNotImplemented` исключение указывают на замысел поддержки нескольких шлюзов, реализован только Stripe |
| **Восстановление удалённых пользователей** | `deleted_at` есть, API для восстановления и блокировки пользователей отсутствует |
| **PATCH/DELETE для OS-образов** | Только создание и список, нет редактирования и удаления |
| **DELETE для IP-адресов** | Только добавление и список |
| **DELETE для планов** | Только создание и обновление |
| **Пользовательский профиль** | Нет эндпоинта GET /users/me или аналогичного |
| **Платёжная история** | Нет пользовательского API для просмотра истории платежей и чеков |
| **Admin-панель по VM** | Нет эндпоинтов для просмотра/управления чужими VM от имени администратора |
| **Управление нодой изнутри** | `grpc_port` в схеме ноды присутствует, но прямое gRPC-соединение API→нода идёт через VM Manager, не напрямую |
| **Rate limiting** | Redis-клиент подключён, явной реализации rate limiting в коде не обнаружено |
| **Email/SMS уведомления** | Нет интеграций для уведомлений пользователей |
| **SSH-ключи** | Упомянуты в `PasswordOrSSHRequired`, но отдельного управления SSH-ключами (хранение, CRUD) нет |
