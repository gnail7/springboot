-- ============================================================
-- 博客模块：建表 + 菜单权限 + 超级管理员授权
-- 约定：BIGINT UNSIGNED 自增主键 / utf8mb4 / 审计字段，与 sys_* 一致
-- ============================================================

-- 1. 分类表（parent_id 支持二级分类，如 前端 → Vue/React；0=顶级）
DROP TABLE IF EXISTS `blog_category`;
CREATE TABLE `blog_category` (
  `category_id`   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id`     BIGINT UNSIGNED DEFAULT 0               COMMENT '父分类ID（0=顶级）',
  `category_name` VARCHAR(50)     NOT NULL                COMMENT '分类名称',
  `order_num`     INT             DEFAULT 0               COMMENT '排序',
  `status`        CHAR(1)         DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `create_by`     VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
  `create_time`   DATETIME        DEFAULT NULL            COMMENT '创建时间',
  `update_by`     VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
  `update_time`   DATETIME        DEFAULT NULL            COMMENT '更新时间',
  `remark`        VARCHAR(500)    DEFAULT ''              COMMENT '备注',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_name` (`category_name`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客分类表';

-- 2. 标签表
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag` (
  `tag_id`      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name`    VARCHAR(50)     NOT NULL                COMMENT '标签名称',
  `order_num`   INT             DEFAULT 0               COMMENT '排序',
  `status`      CHAR(1)         DEFAULT '0'             COMMENT '状态（0正常 1停用）',
  `create_by`   VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
  `create_time` DATETIME        DEFAULT NULL            COMMENT '创建时间',
  `update_by`   VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
  `update_time` DATETIME        DEFAULT NULL            COMMENT '更新时间',
  `remark`      VARCHAR(500)    DEFAULT ''              COMMENT '备注',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_tag_name` (`tag_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客标签表';

-- 3. 文章表（一篇文章属于一个分类；content 存 Markdown）
DROP TABLE IF EXISTS `blog_post`;
CREATE TABLE `blog_post` (
  `post_id`        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `category_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '分类ID',
  `author_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '作者ID（关联 sys_user）',
  `title`          VARCHAR(200)    NOT NULL                COMMENT '标题',
  `slug`           VARCHAR(200)    DEFAULT NULL            COMMENT 'URL别名（可选，唯一）',
  `summary`        VARCHAR(500)    DEFAULT ''              COMMENT '摘要',
  `content`        LONGTEXT                                COMMENT '正文（Markdown）',
  `cover`          VARCHAR(255)    DEFAULT ''              COMMENT '封面图',
  `status`         CHAR(1)         DEFAULT '0'             COMMENT '状态（0草稿 1已发布 2下架）',
  `is_top`         INT             DEFAULT 0               COMMENT '是否置顶（0否 1是）',
  `is_recommend`   INT             DEFAULT 0               COMMENT '是否推荐（0否 1是）',
  `view_count`     INT             DEFAULT 0               COMMENT '浏览量',
  `like_count`     INT             DEFAULT 0               COMMENT '点赞数',
  `comment_count`  INT             DEFAULT 0               COMMENT '评论数',
  `word_count`     INT             DEFAULT 0               COMMENT '字数',
  `published_time` DATETIME        DEFAULT NULL            COMMENT '发布时间',
  `del_flag`       CHAR(1)         DEFAULT '0'             COMMENT '删除标志（0存在 1删除）',
  `create_by`      VARCHAR(64)     DEFAULT ''              COMMENT '创建者',
  `create_time`    DATETIME        DEFAULT NULL            COMMENT '创建时间',
  `update_by`      VARCHAR(64)     DEFAULT ''              COMMENT '更新者',
  `update_time`    DATETIME        DEFAULT NULL            COMMENT '更新时间',
  `remark`         VARCHAR(500)    DEFAULT ''              COMMENT '备注',
  PRIMARY KEY (`post_id`),
  UNIQUE KEY `uk_slug` (`slug`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_published_time` (`published_time`),
  KEY `idx_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客文章表';

-- 4. 文章-标签关联表（多对多）
DROP TABLE IF EXISTS `blog_post_tag`;
CREATE TABLE `blog_post_tag` (
  `post_id` BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
  `tag_id`  BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`post_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关联表';

-- ============================================================
-- 菜单权限（挂到 sidebar：博客管理 -> 文章/分类/标签）＋按钮权限
-- menu_id 在 300~333，避免与现有 1-5/100-104/200-204 冲突
-- ============================================================
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_time`) VALUES
(300, '博客管理', 0,  20, 'blog', NULL, 'M', '0', '0', NULL, 'el-icon-document', NOW()),
(301, '文章管理', 300, 1, 'post', 'blog/post/index', 'C', '0', '0', 'blog:post:list', 'el-icon-edit', NOW()),
(302, '分类管理', 300, 2, 'category', 'blog/category/index', 'C', '0', '0', 'blog:category:list', 'el-icon-folder', NOW()),
(303, '标签管理', 300, 3, 'tag', 'blog/tag/index', 'C', '0', '0', 'blog:tag:list', 'el-icon-price-tag', NOW()),
(311, '文章新增', 301, 1, '', NULL, 'F', '0', '0', 'blog:post:add', '#', NOW()),
(312, '文章修改', 301, 2, '', NULL, 'F', '0', '0', 'blog:post:edit', '#', NOW()),
(313, '文章删除', 301, 3, '', NULL, 'F', '0', '0', 'blog:post:remove', '#', NOW()),
(321, '分类新增', 302, 1, '', NULL, 'F', '0', '0', 'blog:category:add', '#', NOW()),
(322, '分类修改', 302, 2, '', NULL, 'F', '0', '0', 'blog:category:edit', '#', NOW()),
(323, '分类删除', 302, 3, '', NULL, 'F', '0', '0', 'blog:category:remove', '#', NOW()),
(331, '标签新增', 303, 1, '', NULL, 'F', '0', '0', 'blog:tag:add', '#', NOW()),
(332, '标签修改', 303, 2, '', NULL, 'F', '0', '0', 'blog:tag:edit', '#', NOW()),
(333, '标签删除', 303, 3, '', NULL, 'F', '0', '0', 'blog:tag:remove', '#', NOW());

-- 超级管理员(role_id=1) 拥有全部博客菜单/按钮权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 300), (1, 301), (1, 302), (1, 303),
(1, 311), (1, 312), (1, 313),
(1, 321), (1, 322), (1, 323),
(1, 331), (1, 332), (1, 333);

-- 示例数据：分类 + 标签
INSERT INTO `blog_category` (`category_id`, `parent_id`, `category_name`, `order_num`, `status`, `create_time`) VALUES
(1, 0, '前端', 1, '0', NOW()),
(2, 0, '后端', 2, '0', NOW()),
(3, 0, '数据库', 3, '0', NOW()),
(4, 0, '运维', 4, '0', NOW());

INSERT INTO `blog_tag` (`tag_id`, `tag_name`, `order_num`, `status`, `create_time`) VALUES
(1, 'Vue', 1, '0', NOW()),
(2, 'React', 2, '0', NOW()),
(3, 'Java', 3, '0', NOW()),
(4, 'MySQL', 4, '0', NOW()),
(5, 'Spring Boot', 5, '0', NOW()),
(6, 'TypeScript', 6, '0', NOW());
