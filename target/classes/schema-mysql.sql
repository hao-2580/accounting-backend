-- 企业会计管理系统 数据库初始化脚本 (MySQL)

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    real_name   VARCHAR(50) COMMENT '真实姓名',
    email       VARCHAR(100) COMMENT '邮箱',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用',
    deleted     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

-- 账户表
CREATE TABLE IF NOT EXISTS account (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL COMMENT '账户名称',
    type        VARCHAR(50)  NOT NULL COMMENT '账户类型',
    balance     DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    remark      VARCHAR(255) COMMENT '备注',
    deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户';

-- 收支分类表
CREATE TABLE IF NOT EXISTS category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL COMMENT '所属用户',
    name        VARCHAR(100) NOT NULL COMMENT '分类名称',
    type        VARCHAR(10)  NOT NULL COMMENT '类型:INCOME/EXPENSE',
    icon        VARCHAR(50) COMMENT '图标',
    sort_order  INT          NOT NULL DEFAULT 0,
    deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收支分类';

-- 收支记录表
CREATE TABLE IF NOT EXISTS transaction (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    type          VARCHAR(10)   NOT NULL COMMENT '类型:INCOME/EXPENSE',
    amount        DECIMAL(18,2) NOT NULL COMMENT '金额',
    category      VARCHAR(100) COMMENT '分类',
    date          DATE          NOT NULL COMMENT '日期',
    note          VARCHAR(500) COMMENT '备注',
    account_id    BIGINT        NOT NULL COMMENT '账户ID',
    deleted       TINYINT(1)    NOT NULL DEFAULT 0,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_transaction_date (date),
    INDEX idx_transaction_account (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收支记录';

-- 客户表
CREATE TABLE IF NOT EXISTS client (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL COMMENT '客户名称',
    contact     VARCHAR(100) COMMENT '联系人',
    phone       VARCHAR(50) COMMENT '电话',
    email       VARCHAR(100) COMMENT '邮箱',
    tax_no      VARCHAR(100) COMMENT '税号',
    address     VARCHAR(500) COMMENT '地址',
    deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_client_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户';

-- 客户表2
CREATE TABLE IF NOT EXISTS customer (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL COMMENT '客户名称',
    contact     VARCHAR(100) COMMENT '联系人',
    phone       VARCHAR(50) COMMENT '电话',
    email       VARCHAR(100) COMMENT '邮箱',
    tax_id      VARCHAR(100) COMMENT '税号',
    address     VARCHAR(500) COMMENT '地址',
    deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户2';

-- 发票表
CREATE TABLE IF NOT EXISTS invoice (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_no    VARCHAR(100)  NOT NULL COMMENT '发票编号',
    client_name   VARCHAR(200)  NOT NULL COMMENT '客户名称',
    issue_date    DATE          NOT NULL COMMENT '开票日期',
    due_date      DATE          NOT NULL COMMENT '到期日期',
    status        VARCHAR(20)   NOT NULL DEFAULT 'DRAFT' COMMENT '状态:DRAFT/UNPAID/PARTIAL/PAID',
    tax_rate      DECIMAL(5,2)  NOT NULL DEFAULT 13.00 COMMENT '税率',
    remark        VARCHAR(500) COMMENT '备注',
    deleted       TINYINT(1)    NOT NULL DEFAULT 0,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_invoice_status (status),
    INDEX idx_invoice_no (invoice_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票';

-- 发票明细表
CREATE TABLE IF NOT EXISTS invoice_item (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id    BIGINT        NOT NULL COMMENT '发票ID',
    description   VARCHAR(500)  NOT NULL COMMENT '描述',
    quantity      INT           NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price    DECIMAL(18,2) NOT NULL COMMENT '单价',
    deleted       TINYINT(1)    NOT NULL DEFAULT 0,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_invoice_item_invoice_id (invoice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票明细';
