CREATE TABLE IF NOT EXISTS mall_outbox_event (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    aggregate_id VARCHAR(64) NOT NULL,
    payload_version INT NOT NULL DEFAULT 1,
    payload_json JSON NOT NULL,
    trace_id VARCHAR(64) NULL,
    publish_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    next_retry_at DATETIME NULL,
    published_at DATETIME NULL,
    last_error VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mall_outbox_event_id (event_id),
    KEY idx_mall_outbox_publish (publish_status, next_retry_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_message_consume_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    consumer_group VARCHAR(100) NOT NULL,
    event_id VARCHAR(64) NOT NULL,
    consume_status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
    error_message VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mall_message_consume (consumer_group, event_id),
    KEY idx_mall_message_consume_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mall_job_execution (
    id BIGINT NOT NULL AUTO_INCREMENT,
    job_name VARCHAR(100) NOT NULL,
    execution_id VARCHAR(64) NOT NULL,
    execution_status VARCHAR(32) NOT NULL,
    started_at DATETIME NOT NULL,
    finished_at DATETIME NULL,
    processed_count INT NOT NULL DEFAULT 0,
    duration_ms BIGINT NULL,
    error_message VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mall_job_execution_id (execution_id),
    KEY idx_mall_job_execution_name_created (job_name, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
