CREATE TABLE IF NOT EXISTS mall_inventory (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sku_id BIGINT NOT NULL,
    available_stock INT NOT NULL DEFAULT 0,
    locked_stock INT NOT NULL DEFAULT 0,
    sold_stock INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mall_inventory_sku (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_inventory_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sku_id BIGINT NOT NULL,
    business_type VARCHAR(32) NOT NULL,
    business_no VARCHAR(64) NULL,
    quantity INT NOT NULL,
    before_available INT NOT NULL,
    after_available INT NOT NULL,
    before_locked INT NOT NULL,
    after_locked INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_mall_inventory_log_sku_created (sku_id, created_at),
    KEY idx_mall_inventory_log_business (business_type, business_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
