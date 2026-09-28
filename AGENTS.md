# Spring Boot 项目协作规范

## 项目技术栈

- Spring Boot 4.0.7、Spring MVC、Java 17
- Maven Wrapper：Windows 使用 `./mvnw.cmd`
- MyBatis-Plus + XML Mapper，不使用 JPA
- MySQL、Redis、Spring Security、自定义 JWT 拦截器
- Springdoc OpenAPI、Jakarta Validation

## 分层约定

```text
Controller -> Service -> Mapper/XML -> MySQL
                 |
                 +-> Redis / external services
```

- Controller 只负责 HTTP 参数、校验、状态码和调用一个 Service 方法。
- Service 负责业务规则、事务和跨 Mapper 编排。
- Mapper 只负责数据访问；复杂 SQL 放在对应 XML 中，不把业务判断写进 XML。
- 新接口使用独立的 Request DTO 和 Response DTO，不直接暴露数据库 Entity。
- 依赖使用构造器注入，不使用字段注入。
- 异常统一由 `@RestControllerAdvice` 处理，不在 Controller 中复制 try/catch。

## 现有接口契约

- 普通接口沿用现有的 `Result<T>` 和 `PageResult<T>`，不要新增第二套 `ApiResponse`。
- 新增分页接口必须限制 page size，并校验可排序字段。
- 保留现有 `/api/**` 的 JWT 保护规则；登录、注册和明确的公开接口才允许例外。
- Demo Hub 的公开体验接口使用 `/demo-hub/**`，不要求登录，但必须有参数校验、限流和会话过期控制。
- SSE 接口使用 `text/event-stream`，不要强行套普通 JSON 响应包装。

## Redis 约定

- Key 使用 `springboot:{domain}:{resource}:{id}` 格式，并集中维护在 `RedisKeys` 或对应模块的 Key 类中。
- 所有临时会话、匿名参与者、倒计时和限流数据都必须设置 TTL。
- 优先缓存 DTO 或稳定 JSON，不缓存带懒加载关系的 Entity。
- Demo Hub 的高频实时事件放 Redis Stream/PubSub，不逐条写 MySQL。

## 数据库约定

- 当前项目使用 MyBatis-Plus 和增量 SQL；不要直接引入 JPA Repository、Hibernate 注解或 `@DataJpaTest`。
- 在项目明确迁移到 Flyway 之前，数据库变更使用带用途和顺序的增量 SQL 文件。
- 新表必须有主键、创建时间；会话、匿名参与者和事件表需要过期索引或清理策略。

## 测试与验证

- 业务规则优先写无 Spring Context 的单元测试。
- Controller 使用 MVC slice 测试，Service 使用 Mockito；MyBatis/Redis 集成测试必须显式准备对应依赖。
- 改动后至少运行 `./mvnw.cmd -DskipTests package`；涉及行为时补充 `./mvnw.cmd test`。
- 测试命名使用 `method_condition_expectedBehavior`。

## Demo Hub 特殊规则

- Demo 目录信息、运行会话、匿名参与者是公共模型；不要为 SSE、并发、Redis、MQ 等每种 Demo 重复建表。
- Demo 具体差异通过 `demo_type`、`interaction_mode`、`handler_key` 和配置 JSON 表达。
- `anonymousId` 只标识当前浏览器，不代表登录用户；二维码中的会话 Token 必须短期有效且不可预测。
- 不允许把用户输入当作 Java、SQL、Shell 或容器命令执行。
