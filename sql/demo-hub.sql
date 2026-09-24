-- Demo Hub 通用模型：不按 SSE、并发、MQ 等 Demo 类型拆表。
-- 实时连接和临时事件建议放 Redis，以下表只保存目录、运行记录和可选的最终结果。

CREATE TABLE IF NOT EXISTS demo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '稳定的 Demo 标识',
    name VARCHAR(200) NOT NULL COMMENT '展示名称',
    description VARCHAR(1000) COMMENT 'Demo 说明',
    demo_type VARCHAR(50) NOT NULL COMMENT 'SSE/CONCURRENCY/REDIS 等主题',
    handler_key VARCHAR(100) NOT NULL COMMENT '代码处理器标识',
    interaction_mode VARCHAR(30) NOT NULL COMMENT 'STATIC/REQUEST/STREAM/ROOM',
    config_json JSON COMMENT '非敏感配置',
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    sort_no INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Demo Hub 目录';

CREATE TABLE IF NOT EXISTS demo_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    demo_id BIGINT NOT NULL,
    version VARCHAR(50) NOT NULL,
    git_commit VARCHAR(100),
    docker_image VARCHAR(300),
    config_json JSON,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    published_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_demo_version (demo_id, version),
    CONSTRAINT fk_demo_version_demo FOREIGN KEY (demo_id) REFERENCES demo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Demo 版本';

CREATE TABLE IF NOT EXISTS demo_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    demo_id BIGINT NOT NULL,
    version_id BIGINT,
    session_key VARCHAR(100) NOT NULL UNIQUE COMMENT '分享链接或房间使用的随机标识',
    session_type VARCHAR(30) NOT NULL COMMENT 'SINGLE/ROOM/STREAM',
    status VARCHAR(20) NOT NULL COMMENT 'CREATED/RUNNING/FINISHED/EXPIRED',
    state_json JSON COMMENT '运行中状态快照，低频更新',
    result_json JSON COMMENT '结束后的统计结果',
    expires_at DATETIME NOT NULL,
    started_at DATETIME,
    ended_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_demo_session_demo_status (demo_id, status),
    KEY idx_demo_session_expires (expires_at),
    CONSTRAINT fk_demo_session_demo FOREIGN KEY (demo_id) REFERENCES demo (id),
    CONSTRAINT fk_demo_session_version FOREIGN KEY (version_id) REFERENCES demo_version (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Demo 一次运行会话';

CREATE TABLE IF NOT EXISTS demo_participant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    anonymous_id VARCHAR(100) NOT NULL COMMENT '浏览器生成的匿名 UUID，不是登录用户',
    connection_id VARCHAR(150),
    status VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME,
    UNIQUE KEY uk_session_anonymous (session_id, anonymous_id),
    KEY idx_demo_participant_session (session_id),
    CONSTRAINT fk_demo_participant_session FOREIGN KEY (session_id) REFERENCES demo_session (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='多人 Demo 的匿名参与者';

CREATE TABLE IF NOT EXISTS demo_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    participant_id BIGINT,
    event_type VARCHAR(50) NOT NULL,
    event_data JSON,
    seq_no INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_demo_event_session_time (session_id, created_at),
    CONSTRAINT fk_demo_event_session FOREIGN KEY (session_id) REFERENCES demo_session (id),
    CONSTRAINT fk_demo_event_participant FOREIGN KEY (participant_id) REFERENCES demo_participant (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='可选的 Demo 操作事件';

INSERT INTO demo
    (code, name, description, demo_type, handler_key, interaction_mode, config_json, sort_no)
VALUES
    ('sse-stream', 'SSE 实时事件流', '服务端周期性推送事件到浏览器', 'SSE', 'sse-demo', 'STREAM',
     JSON_OBJECT('eventCount', 5, 'intervalMs', 1000), 10),
    ('scan-concurrency', '扫码并发体验', '多人扫码进入同一个体验房间', 'CONCURRENCY', 'concurrency-demo', 'ROOM',
     JSON_OBJECT('maxParticipants', 20, 'ttlSeconds', 600), 20)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    config_json = VALUES(config_json),
    updated_at = CURRENT_TIMESTAMP;
