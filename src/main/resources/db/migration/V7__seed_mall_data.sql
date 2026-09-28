INSERT INTO mall_category (id, parent_id, name, sort_no, status)
VALUES (1001, 0, 'Demo 商品', 1, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status);

INSERT INTO mall_product_spu (id, category_id, title, subtitle, brand, detail, status)
VALUES (2001, 1001, '并发实践演示商品', '用于库存、订单和消息实验', 'Demo Hub', '这是商城高并发实践项目的种子商品。', 'ON_SALE')
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status);

INSERT INTO mall_product_sku (id, spu_id, sku_code, spec_json, sale_price, market_price, status)
VALUES (3001, 2001, 'DEMO-CONCURRENCY-001', '{"version":"v1"}', 9.90, 19.90, 'ON_SALE')
ON DUPLICATE KEY UPDATE sale_price = VALUES(sale_price), status = VALUES(status);

INSERT INTO mall_inventory (sku_id, available_stock, locked_stock, sold_stock, version)
VALUES (3001, 100, 0, 0, 0)
ON DUPLICATE KEY UPDATE sku_id = VALUES(sku_id);
