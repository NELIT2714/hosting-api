CREATE TABLE IF NOT EXISTS users
(
    id_user        BIGSERIAL NOT NULL,
    email          VARCHAR(255),
    password_hash  VARCHAR(255),
    deleted_at     TIMESTAMPTZ DEFAULT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_user),
    UNIQUE (email)
);



CREATE TABLE IF NOT EXISTS users_telegram
(
    id_user_telegram    BIGSERIAL NOT NULL,
    id_user             BIGINT NOT NULL,
    telegram_id         BIGINT NOT NULL,
    first_name          VARCHAR(64) NOT NULL,
    last_name           VARCHAR(64),
    username            VARCHAR(32),
    language_code       VARCHAR(10) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_user_telegram),
    UNIQUE (telegram_id),
    UNIQUE (id_user),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS nodes
(
    id_node             BIGSERIAL NOT NULL,
    node_name           VARCHAR(64) NOT NULL,
    ip_address          VARCHAR(255),
    grpc_port           INTEGER CHECK (grpc_port BETWEEN 1 AND 65535),
    location            VARCHAR(64),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    cert_fingerprint    VARCHAR(64),
    cert_issued_at      TIMESTAMPTZ,
    cert_expires_at     TIMESTAMPTZ,
    last_heartbeat_at   TIMESTAMPTZ,
    revoked_at          TIMESTAMPTZ,
    revoke_reason       VARCHAR(30),

    PRIMARY KEY (id_node),
    UNIQUE (node_name)
);



CREATE TABLE IF NOT EXISTS plans
(
    id_plan             BIGSERIAL NOT NULL,
    plan_name           VARCHAR(64) NOT NULL,
    ram_mb              INTEGER NOT NULL,
    vcpus               INTEGER NOT NULL,
    disk_gb             INTEGER NOT NULL,
    price_per_month     DECIMAL(10,2) NOT NULL,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id_plan),
    UNIQUE (plan_name)
);



CREATE TABLE IF NOT EXISTS virtual_machines
(
    id_vm          BIGSERIAL NOT NULL,
    id_user        BIGINT NOT NULL,
    id_node        BIGINT NOT NULL,
    id_plan        BIGINT NOT NULL,
    vm_name        VARCHAR(20) NOT NULL,
    uuid           UUID DEFAULT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT FALSE,
    is_blocked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at     TIMESTAMPTZ NOT NULL,

    PRIMARY KEY (id_vm),
    UNIQUE (uuid),
    UNIQUE (vm_name),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_node) REFERENCES nodes(id_node)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_plan) REFERENCES plans(id_plan)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_vm_user ON virtual_machines (id_user);
CREATE INDEX IF NOT EXISTS idx_vm_node ON virtual_machines (id_node);
CREATE INDEX IF NOT EXISTS idx_vm_plan ON virtual_machines (id_plan);



CREATE TABLE IF NOT EXISTS vm_lifecycle
(
    id_lifecycle   SERIAL NOT NULL,
    id_vm          BIGINT NOT NULL UNIQUE,
    blocked_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    delete_at      TIMESTAMPTZ,

    PRIMARY KEY (id_lifecycle),
    FOREIGN KEY (id_vm) REFERENCES virtual_machines(id_vm)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_vm_lifecycle_delete_at ON vm_lifecycle (delete_at) WHERE delete_at IS NOT NULL;



CREATE TABLE IF NOT EXISTS ip_pool
(
    id_ip           SERIAL NOT NULL,
    id_node         BIGINT NOT NULL,
    id_vm           BIGINT,
    ip_address      VARCHAR(45) NOT NULL,

    PRIMARY KEY (id_ip),
    UNIQUE (ip_address),

    FOREIGN KEY (id_node) REFERENCES nodes (id_node)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_vm) REFERENCES virtual_machines (id_vm)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_ip_pool_node ON ip_pool (id_node);
CREATE INDEX IF NOT EXISTS idx_ip_pool_vm ON ip_pool (id_vm);



CREATE TABLE IF NOT EXISTS admins
(
    id_admin       BIGSERIAL NOT NULL,
    id_user        BIGINT NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_admin),
    UNIQUE (id_user),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS admin_permissions
(
    id_admin       BIGINT NOT NULL,
    permission     VARCHAR(30) NOT NULL,

    PRIMARY KEY (id_admin, permission),
    FOREIGN KEY (id_admin) REFERENCES admins(id_admin)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS payments
(
    id_payment              BIGSERIAL NOT NULL,
    id_user                 BIGINT NOT NULL,
    gateway                 VARCHAR(50) NOT NULL,
    gateway_payment_id      VARCHAR(255),
    amount                  DECIMAL(18,8) NOT NULL,
    currency                VARCHAR(10) NOT NULL DEFAULT 'EUR',
    status                  VARCHAR(50) NOT NULL,
    type                    VARCHAR(50) NOT NULL,
    gateway_payload         JSONB,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_payment),
    UNIQUE (gateway, gateway_payment_id),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_payments_user ON payments (id_user);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments (status);



CREATE TABLE IF NOT EXISTS os_images
(
    id_os_image    BIGSERIAL NOT NULL,
    image_name     VARCHAR(50) NOT NULL,
    file_name      VARCHAR(100) NOT NULL,

    PRIMARY KEY (id_os_image),
    UNIQUE(image_name),
    UNIQUE(file_name)
);



CREATE TABLE IF NOT EXISTS vps_orders
(
    id_vps_order    BIGSERIAL NOT NULL,
    id_vm           BIGINT DEFAULT NULL,
    id_payment      BIGINT NOT NULL,
    id_plan         BIGINT NOT NULL,
    id_os_image     BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_vps_order),
    UNIQUE (id_payment),
    UNIQUE (id_vm),

    FOREIGN KEY (id_vm) REFERENCES virtual_machines(id_vm)
        ON DELETE SET NULL ON UPDATE CASCADE,

    FOREIGN KEY (id_payment) REFERENCES payments(id_payment)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    FOREIGN KEY (id_plan) REFERENCES plans(id_plan)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_os_image) REFERENCES os_images(id_os_image)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_vps_orders_plan ON vps_orders (id_plan);
CREATE INDEX IF NOT EXISTS idx_vps_orders_os_image ON vps_orders (id_os_image);



CREATE TABLE IF NOT EXISTS vps_renewal_orders
(
    id_renewal_order  BIGSERIAL NOT NULL,
    id_vm             BIGINT NOT NULL,
    id_payment        BIGINT NOT NULL,
    days              INTEGER NOT NULL DEFAULT 30,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_renewal_order),
    UNIQUE (id_payment),

    FOREIGN KEY (id_vm) REFERENCES virtual_machines(id_vm)
        ON DELETE CASCADE ON UPDATE CASCADE,

    FOREIGN KEY (id_payment) REFERENCES payments(id_payment)
        ON DELETE RESTRICT ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_renewal_orders_vm ON vps_renewal_orders (id_vm);



CREATE TABLE IF NOT EXISTS receipts
(
    id_receipt          BIGSERIAL NOT NULL,
    id_payment          BIGINT NOT NULL,
    number              VARCHAR(50) NOT NULL,
    buyer_name          VARCHAR(255) NOT NULL,
    buyer_address       TEXT NOT NULL,
    service_description TEXT NOT NULL,
    issued_at           TIMESTAMPTZ NOT NULL,

    PRIMARY KEY (id_receipt),
    UNIQUE (number),
    UNIQUE (id_payment),

    FOREIGN KEY (id_payment) REFERENCES payments(id_payment)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);



CREATE TABLE IF NOT EXISTS sessions
(
    id_session           BIGSERIAL NOT NULL,
    id_user              BIGINT NOT NULL,
    refresh_token_hash   VARCHAR(255) NOT NULL,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at           TIMESTAMPTZ NOT NULL,
    revoked_at           TIMESTAMPTZ,
    revoke_reason        VARCHAR(30),

    PRIMARY KEY (id_session),
    UNIQUE (refresh_token_hash),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_sessions_user ON sessions (id_user);
CREATE INDEX IF NOT EXISTS idx_sessions_active_expiry ON sessions (expires_at) WHERE revoked_at IS NULL;



CREATE TABLE IF NOT EXISTS promo_codes
(
    id_promo            BIGSERIAL NOT NULL,
    code                VARCHAR(32) NOT NULL,
    amount_of_uses      INTEGER DEFAULT NULL CHECK (amount_of_uses IS NULL OR amount_of_uses >= 0),
    discount            SMALLINT NOT NULL CHECK (discount BETWEEN 1 AND 100),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    expires_at          TIMESTAMPTZ DEFAULT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_promo),
    UNIQUE (code)
);



CREATE TABLE IF NOT EXISTS promo_codes_uses
(
    id_promo_use        BIGSERIAL NOT NULL,
    id_user             BIGINT NOT NULL,
    id_code             BIGINT NOT NULL,
    used_at             TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id_promo_use),
    UNIQUE (id_user, id_code),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_code) REFERENCES promo_codes(id_promo)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_promo_codes_uses_code ON promo_codes_uses (id_code);
