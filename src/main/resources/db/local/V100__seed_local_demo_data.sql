-- Development-only seed data. This location is enabled only by application-local.yml.
-- INSERT IGNORE prevents duplicate-key errors when the legacy seed data already exists.

INSERT IGNORE INTO `sys_dept`
(`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `status`, `del_flag`, `create_time`) VALUES
(1, 0, '0', '总公司', 0, '0', '0', NOW()),
(2, 1, '0,1', '研发部门', 1, '0', '0', NOW()),
(3, 1, '0,1', '市场部门', 2, '0', '0', NOW()),
(4, 1, '0,1', '测试部门', 3, '0', '0', NOW()),
(5, 1, '0,1', '财务部门', 4, '0', '0', NOW());

INSERT IGNORE INTO `sys_post`
(`post_id`, `post_code`, `post_name`, `order_num`, `status`, `create_time`) VALUES
(1, 'ceo', '董事长', 1, '0', NOW()),
(2, 'se', '高级工程师', 2, '0', NOW()),
(3, 'sse', '架构师', 3, '0', NOW()),
(4, 'pm', '产品经理', 4, '0', NOW());

-- Local demo account only. Replace/disable this credential outside a local environment.
INSERT IGNORE INTO `sys_user`
(`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phone`, `sex`, `avatar`, `password`, `status`, `del_flag`, `create_time`) VALUES
(1, 2, 'admin', '系统管理员', '00', 'admin@example.com', '13800138000', '0', '', '$2a$10$7JB720yubVS6vJ5xK3L/4OQZ3W5qGJmV2p1Lk8nM9oP0QrStUvWx', '0', '0', NOW()),
(2, 3, 'zhangsan', '张三', '00', 'zhangsan@example.com', '13800138001', '0', '', '$2a$10$7JB720yubVS6vJ5xK3L/4OQZ3W5qGJmV2p1Lk8nM9oP0QrStUvWx', '0', '0', NOW());

INSERT IGNORE INTO `sys_role`
(`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_time`) VALUES
(1, '超级管理员', 'admin', 1, '1', '0', '0', NOW()),
(2, '普通角色', 'common', 2, '2', '0', '0', NOW());

INSERT IGNORE INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_time`) VALUES
(1, '系统管理', 0, 1, 'system', NULL, 'M', '0', '0', NULL, 'el-icon-setting', NOW()),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', '0', '0', 'system:user:list', 'el-icon-user', NOW()),
(3, '角色管理', 1, 2, 'role', 'system/role/index', 'C', '0', '0', 'system:role:list', 'el-icon-s-custom', NOW()),
(4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', '0', '0', 'system:menu:list', 'el-icon-menu', NOW()),
(5, '部门管理', 1, 4, 'dept', 'system/dept/index', 'C', '0', '0', 'system:dept:list', 'el-icon-office-building', NOW()),
(100, '用户查询', 2, 1, '', NULL, 'F', '0', '0', 'system:user:query', '#', NOW()),
(101, '用户新增', 2, 2, '', NULL, 'F', '0', '0', 'system:user:add', '#', NOW()),
(102, '用户修改', 2, 3, '', NULL, 'F', '0', '0', 'system:user:edit', '#', NOW()),
(103, '用户删除', 2, 4, '', NULL, 'F', '0', '0', 'system:user:remove', '#', NOW()),
(104, '用户导出', 2, 5, '', NULL, 'F', '0', '0', 'system:user:export', '#', NOW()),
(200, '角色查询', 3, 1, '', NULL, 'F', '0', '0', 'system:role:query', '#', NOW()),
(201, '角色新增', 3, 2, '', NULL, 'F', '0', '0', 'system:role:add', '#', NOW()),
(202, '角色修改', 3, 3, '', NULL, 'F', '0', '0', 'system:role:edit', '#', NOW()),
(203, '角色删除', 3, 4, '', NULL, 'F', '0', '0', 'system:role:remove', '#', NOW()),
(204, '角色导出', 3, 5, '', NULL, 'F', '0', '0', 'system:role:export', '#', NOW());

INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.user_id, r.role_id
FROM sys_user u
CROSS JOIN sys_role r
WHERE (u.user_name = 'admin' AND r.role_key = 'admin')
   OR (u.user_name = 'zhangsan' AND r.role_key = 'common');

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM sys_role r
CROSS JOIN sys_menu m
WHERE r.role_key = 'admin';

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM sys_role r
CROSS JOIN sys_menu m
WHERE r.role_key = 'common'
  AND m.menu_id IN (1, 2, 3, 4, 5, 100, 200);

INSERT IGNORE INTO `sys_menu`
(`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_time`) VALUES
(300, '博客管理', 0, 20, 'blog', NULL, 'M', '0', '0', NULL, 'el-icon-document', NOW()),
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

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM sys_role r
CROSS JOIN sys_menu m
WHERE r.role_key = 'admin'
  AND m.menu_id BETWEEN 300 AND 333;

INSERT IGNORE INTO `blog_category`
(`category_id`, `parent_id`, `category_name`, `order_num`, `status`, `create_time`) VALUES
(1, 0, '前端', 1, '0', NOW()),
(2, 0, '后端', 2, '0', NOW()),
(3, 0, '数据库', 3, '0', NOW()),
(4, 0, '运维', 4, '0', NOW());

INSERT IGNORE INTO `blog_tag`
(`tag_id`, `tag_name`, `order_num`, `status`, `create_time`) VALUES
(1, 'Vue', 1, '0', NOW()),
(2, 'React', 2, '0', NOW()),
(3, 'Java', 3, '0', NOW()),
(4, 'MySQL', 4, '0', NOW()),
(5, 'Spring Boot', 5, '0', NOW()),
(6, 'TypeScript', 6, '0', NOW());
