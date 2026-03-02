-- 企业会计管理系统 数据库初始化脚本 (H2 Compatible)

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    real_name   VARCHAR(50),
    email       VARCHAR(100),
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    enabled     SMALLINT     NOT NULL DEFAULT 1,
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_username ON sys_user(username);

-- 账户表
CREATE TABLE IF NOT EXISTS account (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    balance     DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    currency    VARCHAR(10)  NOT NULL DEFAULT 'CNY',
    description VARCHAR(255),
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_account_user_id ON account(user_id);

-- 收支分类表
CREATE TABLE IF NOT EXISTS category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(10)  NOT NULL,
    icon        VARCHAR(50),
    sort_order  INT          NOT NULL DEFAULT 0,
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_category_user_type ON category(user_id, type);

-- 收支记录表
CREATE TABLE IF NOT EXISTS transactions (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    type          VARCHAR(10)   NOT NULL,
    amount        DECIMAL(18,2) NOT NULL,
    category      VARCHAR(100),
    date          DATE          NOT NULL,
    note          VARCHAR(500),
    account_id    BIGINT        NOT NULL,
    deleted       SMALLINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date);
CREATE INDEX IF NOT EXISTS idx_transactions_account ON transactions(account_id);

-- 客户表
CREATE TABLE IF NOT EXISTS client (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL,
    contact     VARCHAR(100),
    phone       VARCHAR(50),
    email       VARCHAR(100),
    tax_no      VARCHAR(100),
    address     VARCHAR(500),
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_client_user_id ON client(user_id);

-- 发票表
CREATE TABLE IF NOT EXISTS invoices (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_no    VARCHAR(100)  NOT NULL,
    client_name   VARCHAR(200)  NOT NULL,
    issue_date    DATE          NOT NULL,
    due_date      DATE          NOT NULL,
    status        VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    tax_rate      DECIMAL(5,2)  NOT NULL DEFAULT 13.00,
    remark        VARCHAR(500),
    deleted       SMALLINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_invoices_status ON invoices(status);

-- 发票明细表
CREATE TABLE IF NOT EXISTS invoice_item (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id    BIGINT        NOT NULL,
    description   VARCHAR(500)  NOT NULL,
    quantity      INT           NOT NULL DEFAULT 1,
    unit_price    DECIMAL(18,2) NOT NULL,
    sort_order    INT           NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_invoice_item_invoice_id ON invoice_item(invoice_id);
