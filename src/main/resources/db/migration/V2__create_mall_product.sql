CREATE TABLE IF NOT EXISTS mall_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    parent_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    sort_no INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_mall_category_parent_status (parent_id, status, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_product_spu (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    subtitle VARCHAR(500) NULL,
    brand VARCHAR(100) NULL,
    detail TEXT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_mall_product_spu_category_status (category_id, status, id),
    KEY idx_mall_product_spu_status_updated (status, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_product_sku (
    id BIGINT NOT NULL AUTO_INCREMENT,
    spu_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    spec_json JSON NULL,
    sale_price DECIMAL(18,2) NOT NULL,
    market_price DECIMAL(18,2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ON_SALE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mall_product_sku_code (sku_code),
    KEY idx_mall_product_sku_spu_status (spu_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
