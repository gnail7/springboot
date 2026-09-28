# Mall 高并发实践项目

当前项目采用模块化单体：一个 Spring Boot 应用、一个 MySQL 业务库、Redis 和 RabbitMQ。旧的 `sys_*` 与博客表保留不动，商城表统一使用 `mall_` 前缀并由 Flyway 管理。

## 本地启动

```bash
docker compose up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

RabbitMQ 管理台：<http://localhost:15672>，账号 `rabbit`，密码 `rabbit`。

Flyway 在 `local` profile 下启用，并使用 baseline 方式接管已有数据库；首次启动会创建 `mall_*` 表和种子商品，不会修改旧的 `sys_*`、博客表。

## 已落地接口

```text
GET  /api/mall/public/categories
GET  /api/mall/public/products
GET  /api/mall/public/products/{skuId}

POST /api/mall/member/auth/register
POST /api/mall/member/auth/login
POST /api/mall/member/auth/logout

GET  /api/mall/member/cart/items
POST /api/mall/member/cart/items
PUT  /api/mall/member/cart/items/{skuId}
DELETE /api/mall/member/cart/items/{skuId}

POST /api/mall/member/orders
GET  /api/mall/member/orders
GET  /api/mall/member/orders/{orderNo}
POST /api/mall/member/orders/{orderNo}/cancel
```

会员 Token 与后台管理员 Token 分离。会员接口使用 `Authorization: Bearer <member-token>`；订单创建请求必须携带客户端生成的 `idempotencyKey`。

## 实践入口

- `RedisLockService`：`SET NX PX`、Token 校验 Lua 解锁和续期。
- `mall_outbox_event`：订单事务内写事件，定时发布到 RabbitMQ。
- `mall_message_consume_record`：消费者幂等记录。
- `MallOrderTimeoutJob`：批量关闭过期订单并释放锁定库存，使用任务级 Redis 锁。
- `/api/mall/lab/flash-sales/{activityId}/preheat`：local profile 下预热 Redis 库存。
- `/api/mall/lab/flash-sales/{activityId}/orders`：local profile 下执行 Lua 原子扣减和单会员去重实验。

金额使用 `DECIMAL(18,2)`，订单明细保存商品和 SKU 快照；库存扣减使用 `available_stock >= quantity` 加版本号的乐观锁条件，避免并发超卖。
