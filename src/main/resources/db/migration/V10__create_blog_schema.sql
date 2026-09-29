-- Blog schema previously maintained in sql/blog.sql.
-- This migration intentionally does not seed menu or sample content.

CREATE TABLE IF NOT EXISTS `blog_category` (
    `category_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父分类ID（0=顶级）',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `order_num` INT DEFAULT 0 COMMENT '排序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`category_id`),
    UNIQUE KEY `uk_category_name` (`category_name`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客分类表';

CREATE TABLE IF NOT EXISTS `blog_tag` (
    `tag_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `order_num` INT DEFAULT 0 COMMENT '排序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`tag_id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客标签表';

CREATE TABLE IF NOT EXISTS `blog_post` (
    `post_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `category_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '分类ID',
    `author_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '作者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `slug` VARCHAR(200) DEFAULT NULL COMMENT 'URL别名',
    `summary` VARCHAR(500) DEFAULT '' COMMENT '摘要',
    `content` LONGTEXT COMMENT 'Markdown正文',
    `cover` VARCHAR(255) DEFAULT '' COMMENT '封面图',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0草稿 1已发布 2下架）',
    `is_top` INT DEFAULT 0 COMMENT '是否置顶',
    `is_recommend` INT DEFAULT 0 COMMENT '是否推荐',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `word_count` INT DEFAULT 0 COMMENT '字数',
    `published_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`post_id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_published_time` (`published_time`),
    KEY `idx_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客文章表';

CREATE TABLE IF NOT EXISTS `blog_post_tag` (
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`post_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关联表';
