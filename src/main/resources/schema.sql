-- USERS
CREATE TABLE IF NOT EXISTS users
(
    id_user        SERIAL NOT NULL,
    email          VARCHAR(255),
    password_hash  VARCHAR(255),
    created_at     TIMESTAMP NOT NULL,

    PRIMARY KEY (id_user),
    UNIQUE (email)
);



CREATE TABLE IF NOT EXISTS users_telegram
(
    id_user_telegram    SERIAL NOT NULL,
    id_user             INTEGER NOT NULL,
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
    id_node        SERIAL NOT NULL,
    node_name      VARCHAR(64) NOT NULL,
    ip_address     VARCHAR(255) NOT NULL,
    grpc_port      INTEGER NOT NULL,
    location       VARCHAR(64),
    is_active      BOOLEAN DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL,

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
    max_count           INTEGER NOT NULL,
    max_uplink_mbps     INTEGER NOT NULL,
    is_active           BOOLEAN DEFAULT TRUE,

    PRIMARY KEY (id_plan),
    UNIQUE (plan_name)
);



-- VMS
CREATE TABLE IF NOT EXISTS virtual_machines
(
    id_vm          SERIAL NOT NULL,
    id_user        INTEGER NOT NULL,
    id_node        INTEGER NOT NULL,
    id_plan        INTEGER NOT NULL,
    vm_name        VARCHAR(20) NOT NULL,
    uuid           VARCHAR(36) NOT NULL,
    ip_address     VARCHAR(15),
    created_at     TIMESTAMP NOT NULL,

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



-- ADMINS
CREATE TABLE IF NOT EXISTS admins
(
    id_admin       SERIAL NOT NULL,
    id_user        INTEGER NOT NULL,
    created_at     TIMESTAMP NOT NULL,

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