# Demo Hub 基础设计

## 设计原则

Demo Hub 不为每一种 Demo 建一套表。目录、运行会话和参与者是所有交互 Demo 的公共概念；SSE、扫码并发、Redis 锁、消息队列等差异由 `demo_type`、`interaction_mode`、`handler_key` 和 JSON 配置描述。

数据库负责保存可长期查询的数据，Redis 负责连接、倒计时、临时事件和实时广播。SSE 消息不应该逐条写入 MySQL，结束时只保存汇总结果即可。

## 当前可体验接口

无需登录，接口路径不放在 `/api/**` 下：

```text
GET /demo-hub/demos
GET /demo-hub/demos/sse-stream/stream?eventCount=5&intervalMs=1000
```

浏览器控制台可以直接体验：

```javascript
const source = new EventSource('/demo-hub/demos/sse-stream/stream');
source.addEventListener('demo-tick', event => console.log(JSON.parse(event.data)));
source.onerror = () => source.close();
```

## 扩展新的 Demo

1. 在 `DemoCatalog` 中增加目录定义，或将其改为从 `demo` 表读取。
2. 新增一个以 `handlerKey` 标识的 Spring Service。
3. 需要多人参与时创建 `demo_session` 和 `demo_participant`，不要为该 Demo 单独建表。
4. 需要历史回放时再使用 `demo_event`；高频实时事件优先使用 Redis Stream。

并发扫码 Demo 的推荐流程是：创建一个短期 `demo_session`，二维码只携带随机 `session_key` 和过期时间；扫码者使用浏览器生成的匿名 UUID 加入会话，WebSocket 或 SSE 负责实时通知，Redis 保存临时状态，结束时将统计结果写回 `demo_session.result_json`。
