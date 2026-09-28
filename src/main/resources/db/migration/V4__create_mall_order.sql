CREATE TABLE IF NOT EXISTS mall_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL,
    member_id BIGINT NOT NULL,
    order_status VARCHAR(32) NOT NULL DEFAULT 'PENDING_PAYMENT',
    total_amount DECIMAL(18,2) NOT NULL,
    payable_amount DECIMAL(18,2) NOT NULL,
    expire_at DATETIME NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mall_order_no (order_no),
    UNIQUE KEY uk_mall_order_member_idempotency (member_id, idempotency_key),
    KEY idx_mall_order_member_created (member_id, created_at),
    KEY idx_mall_order_status_expire (order_status, expire_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_order_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    sku_id BIGINT NOT NULL,
    spu_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    product_title VARCHAR(200) NOT NULL,
    sku_snapshot JSON NULL,
    quantity INT NOT NULL,
    sale_price DECIMAL(18,2) NOT NULL,
    item_amount DECIMAL(18,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_mall_order_item_order (order_id),
    KEY idx_mall_order_item_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_order_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    from_status VARCHAR(32) NULL,
    to_status VARCHAR(32) NOT NULL,
    operator_type VARCHAR(32) NOT NULL,
    operator_id BIGINT NULL,
    reason VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_mall_order_log_order (order_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
