-- USERS
CREATE TABLE IF NOT EXISTS users
(
    id_user        SERIAL NOT NULL,
    email          VARCHAR(255),
    password_hash  VARCHAR(255),
    created_at     TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id_user),
    UNIQUE (email)
);



CREATE TABLE IF NOT EXISTS users_telegram
(
    id_user_telegram    SERIAL NOT NULL,
    id_user             BIGINT NOT NULL,
    telegram_id         VARCHAR(16) NOT NULL,
    first_name          VARCHAR(64) NOT NULL,
    last_name           VARCHAR(64),
    username            VARCHAR(32),
    language_code       VARCHAR(3) NOT NULL,

    PRIMARY KEY (id_user_telegram),
    UNIQUE (telegram_id),
    UNIQUE (id_user),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- NODES
CREATE TABLE IF NOT EXISTS nodes
(
    id_node             SERIAL NOT NULL,
    node_name           VARCHAR(64) NOT NULL,
    ip_address          VARCHAR(255),           -- nullable, заполняется heartbeat-ом ноды
    grpc_port           INTEGER,                -- nullable, заполняется heartbeat-ом ноды
    location            VARCHAR(64),
    is_active           BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),

    cert_fingerprint    VARCHAR(64),
    cert_issued_at      TIMESTAMP,
    cert_expires_at     TIMESTAMP,
    last_heartbeat_at   TIMESTAMP,
    revoked_at          TIMESTAMP,
    revoke_reason       VARCHAR(30),

    PRIMARY KEY (id_node),
    UNIQUE (node_name)
);

-- PLANS
CREATE TABLE IF NOT EXISTS plans
(
    id_plan             SERIAL NOT NULL,
    plan_name           VARCHAR(64) NOT NULL,
    ram_mb              INTEGER NOT NULL,
    vcpus               INTEGER NOT NULL,
    disk_gb             INTEGER NOT NULL,
    price_per_month     DECIMAL(10,2) NOT NULL,
    is_active           BOOLEAN DEFAULT TRUE,

    PRIMARY KEY (id_plan),
    UNIQUE (plan_name)
);



-- VMS
CREATE TABLE IF NOT EXISTS virtual_machines
(
    id_vm          SERIAL NOT NULL,
    id_user        BIGINT NOT NULL,
    id_node        BIGINT NOT NULL,
    id_plan        BIGINT NOT NULL,
    vm_name        VARCHAR(20) NOT NULL,
    uuid           VARCHAR(36) DEFAULT NULL,
    is_active      BOOLEAN NOT NULL DEFAULT FALSE,
    is_blocked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    expires_at     TIMESTAMP NOT NULL,

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



-- VM LIFECYCLE
CREATE TABLE IF NOT EXISTS vm_lifecycle
(
    id_lifecycle   SERIAL NOT NULL,
    id_vm          BIGINT NOT NULL UNIQUE,
    blocked_at     TIMESTAMP NOT NULL DEFAULT now(),
    delete_at      TIMESTAMP,

    PRIMARY KEY (id_lifecycle),
    FOREIGN KEY (id_vm) REFERENCES virtual_machines(id_vm)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- IP ADDRESSES
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



-- ADMINS
CREATE TABLE IF NOT EXISTS admins
(
    id_admin       SERIAL NOT NULL,
    id_user        BIGINT NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id_admin),
    UNIQUE (id_user),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- ADMIN PERMISSIONS
CREATE TABLE IF NOT EXISTS admin_permissions
(
    id_admin       INTEGER NOT NULL,
    permission     VARCHAR(30) NOT NULL,

    PRIMARY KEY (id_admin, permission),
    FOREIGN KEY (id_admin) REFERENCES admins(id_admin)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- PAYMENTS
CREATE TABLE IF NOT EXISTS payments
(
    id_payment              SERIAL NOT NULL,
    id_user                 BIGINT NOT NULL,
    gateway                 VARCHAR(50) NOT NULL,
    gateway_payment_id      VARCHAR(255),
    amount                  DECIMAL(10, 2) NOT NULL,
    currency                VARCHAR(3) NOT NULL DEFAULT 'EUR',
    status                  VARCHAR(50) NOT NULL,   -- PENDING, SUCCEEDED, FAILED
    type                    VARCHAR(50) NOT NULL,   -- VPS_PURCHASE, DISK_ADDON, IP_ADDON
    created_at              TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id_payment),
    UNIQUE (gateway, gateway_payment_id),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- OS images
CREATE TABLE IF NOT EXISTS os_images
(
    id_os_image    SERIAL NOT NULL,
    image_name     VARCHAR(50) NOT NULL,
    file_name      VARCHAR(100) NOT NULL,

    PRIMARY KEY (id_os_image),
    UNIQUE(image_name),
    UNIQUE(file_name)
);



-- VPS
CREATE TABLE IF NOT EXISTS vps_orders
(
    id_vps_order    SERIAL NOT NULL,
    id_vm           BIGINT DEFAULT NULL,
    id_payment      BIGINT NOT NULL,
    id_plan         BIGINT NOT NULL,
    id_os_image     BIGINT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id_vps_order),
    UNIQUE (id_payment),
    UNIQUE (id_vm),

    FOREIGN KEY (id_vm) REFERENCES virtual_machines(id_vm)
        ON DELETE SET NULL ON UPDATE CASCADE,

    FOREIGN KEY (id_payment) REFERENCES payments(id_payment)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_plan) REFERENCES plans(id_plan)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (id_os_image) REFERENCES os_images(id_os_image)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- VPS RENEWAL
CREATE TABLE IF NOT EXISTS vps_renewal_orders
(
    id_renewal_order  SERIAL NOT NULL,
    id_vm             BIGINT NOT NULL,
    id_payment        BIGINT NOT NULL,
    days              INTEGER NOT NULL DEFAULT 30,
    created_at        TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id_renewal_order),
    UNIQUE (id_payment),

    FOREIGN KEY (id_vm) REFERENCES virtual_machines(id_vm)
        ON DELETE CASCADE ON UPDATE CASCADE,

    FOREIGN KEY (id_payment) REFERENCES payments(id_payment)
        ON DELETE CASCADE ON UPDATE CASCADE
);



-- RECEIPTS
CREATE TABLE IF NOT EXISTS receipts
(
    id_receipt          SERIAL NOT NULL,
    id_payment          BIGINT NOT NULL,
    number              VARCHAR(50) NOT NULL,
    buyer_name          VARCHAR(255) NOT NULL,
    buyer_address       TEXT NOT NULL,
    service_description TEXT NOT NULL,
    issued_at           TIMESTAMP NOT NULL,

    PRIMARY KEY (id_receipt),
    UNIQUE (number),
    UNIQUE (id_payment),

    FOREIGN KEY (id_payment) REFERENCES payments(id_payment)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- SESSIONS
CREATE TABLE IF NOT EXISTS sessions
(
    id_session           SERIAL NOT NULL,
    id_user              BIGINT NOT NULL,
    refresh_token_hash   VARCHAR(255) NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    expires_at           TIMESTAMP NOT NULL,
    revoked_at           TIMESTAMP,
    revoke_reason        VARCHAR(30),

    PRIMARY KEY (id_session),
    UNIQUE (refresh_token_hash),

    FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
