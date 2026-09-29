-- Legacy RBAC/system schema previously documented in root sql.md.
-- Never drop or recreate existing tables here: this migration must be safe for
-- databases that already contain the legacy schema before Flyway was enabled.

CREATE TABLE IF NOT EXISTS `sys_dept` (
    `dept_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父部门ID（顶级为0）',
    `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `order_num` INT DEFAULT 0 COMMENT '显示排序',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`dept_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS `sys_post` (
    `post_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code` VARCHAR(64) NOT NULL COMMENT '岗位编码',
    `post_name` VARCHAR(50) NOT NULL COMMENT '岗位名称',
    `order_num` INT DEFAULT 0 COMMENT '显示排序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`post_id`),
    UNIQUE KEY `uk_post_code` (`post_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

CREATE TABLE IF NOT EXISTS `sys_user` (
    `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '部门ID',
    `user_name` VARCHAR(64) NOT NULL COMMENT '登录账号',
    `nick_name` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `user_type` VARCHAR(2) DEFAULT '00' COMMENT '用户类型',
    `email` VARCHAR(100) DEFAULT '' COMMENT '用户邮箱',
    `phone` VARCHAR(11) DEFAULT '' COMMENT '手机号码',
    `sex` CHAR(1) DEFAULT '0' COMMENT '性别',
    `avatar` VARCHAR(200) DEFAULT '' COMMENT '头像地址',
    `password` VARCHAR(100) DEFAULT '' COMMENT 'BCrypt 密码',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    `login_ip` VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
    `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_user_name` (`user_name`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `sys_role` (
    `role_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(100) NOT NULL COMMENT '角色标识',
    `role_sort` INT NOT NULL COMMENT '显示顺序',
    `data_scope` CHAR(1) DEFAULT '1' COMMENT '数据范围',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_role_key` (`role_key`),
    KEY `idx_status` (`status`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS `sys_menu` (
    `menu_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父菜单ID',
    `order_num` INT DEFAULT 0 COMMENT '显示顺序',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `query` VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    `is_frame` INT DEFAULT 1 COMMENT '是否外链',
    `is_cache` INT DEFAULT 0 COMMENT '是否缓存',
    `menu_type` CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible` CHAR(1) DEFAULT '0' COMMENT '显示状态',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`menu_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_menu_type` (`menu_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

CREATE TABLE IF NOT EXISTS `sys_user_post` (
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `post_id` BIGINT UNSIGNED NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (`user_id`, `post_id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位关联表';

CREATE TABLE IF NOT EXISTS `sys_role_dept` (
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `dept_id` BIGINT UNSIGNED NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`role_id`, `dept_id`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';
