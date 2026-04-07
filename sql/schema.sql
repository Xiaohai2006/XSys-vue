CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(64) NULL,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(128) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES sys_permission (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_setting (
    s_key VARCHAR(64) NOT NULL PRIMARY KEY,
    s_value VARCHAR(512) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NULL,
    title VARCHAR(128) NOT NULL,
    path VARCHAR(255) NOT NULL DEFAULT '',
    icon VARCHAR(64) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    permission_code VARCHAR(128) NULL,
    menu_type VARCHAR(16) NOT NULL DEFAULT 'menu',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES sys_menu (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_menu_role (
    menu_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (menu_id, role_id),
    CONSTRAINT fk_mr_menu FOREIGN KEY (menu_id) REFERENCES sys_menu (id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_role FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- 基础数据：与 DatabaseSeeder 逻辑一致，便于纯 SQL 初始化或手工导入。
-- 默认用户 admin / admin123（密码为 BCrypt，与 BCryptPasswordEncoder 一致）

-- 权限
INSERT IGNORE INTO sys_permission (code, name, description) VALUES
    ('system:user:read', '查看用户', '用户列表'),
    ('system:user:write', '管理用户角色', '分配用户角色'),
    ('system:role:read', '查看角色权限', '角色与权限列表'),
    ('system:role:write', '管理角色权限', '增删改角色及权限绑定'),
    ('system:settings:read', '查看系统设置', '默认注册角色等'),
    ('system:settings:write', '修改系统设置', '默认注册角色等'),
    ('system:menu:read', '查看菜单', '侧边栏与菜单管理页'),
    ('system:menu:write', '管理菜单', '增删改菜单');

-- 角色
INSERT IGNORE INTO sys_role (code, name, description) VALUES
    ('ADMIN', '管理员', '内置管理员'),
    ('USER', '普通用户', '默认注册用户');

-- 管理员角色绑定全部权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
         CROSS JOIN sys_permission p
WHERE r.code = 'ADMIN';

-- 默认用户 admin（若已存在则跳过）
INSERT INTO sys_user (username, password, nickname, enabled)
SELECT 'admin',
       '$2b$10$0AIoypyGpRtfA.XsNAIJL.48nGdqny.Fk2a3SqerkOpNa2osmd6pq',
       '管理员',
       1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin');

-- admin 绑定 ADMIN 角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
         INNER JOIN sys_role r ON r.code = 'ADMIN'
WHERE u.username = 'admin';

-- 新用户默认角色（USER）
INSERT INTO sys_setting (s_key, s_value)
SELECT 'default_register_role_id', CAST(r.id AS CHAR)
FROM sys_role r
WHERE r.code = 'USER'
  AND NOT EXISTS (SELECT 1 FROM sys_setting WHERE s_key = 'default_register_role_id');


-- 创建菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `path`, `icon`, `sort_order`, `permission_code`, `menu_type`, `enabled`) VALUES (1, NULL, '概览', '/admin', 'HomeOutline', 0, NULL, 'menu', 1);
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `path`, `icon`, `sort_order`, `permission_code`, `menu_type`, `enabled`) VALUES (2, NULL, '用户', '/admin/users', 'PeopleOutline', 1, 'system:user:read', 'menu', 1);
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `path`, `icon`, `sort_order`, `permission_code`, `menu_type`, `enabled`) VALUES (3, NULL, '角色管理', '/admin/roles', 'ListOutline', 2, 'system:role:read', 'menu', 1);
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `path`, `icon`, `sort_order`, `permission_code`, `menu_type`, `enabled`) VALUES (4, NULL, '权限管理', '/admin/permissions', 'ListOutline', 3, 'system:role:read', 'menu', 1);
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `path`, `icon`, `sort_order`, `permission_code`, `menu_type`, `enabled`) VALUES (5, NULL, '系统设置', '/admin/settings', 'SettingsOutline', 5, 'system:settings:read', 'menu', 1);
INSERT INTO `sys_menu` (`id`, `parent_id`, `title`, `path`, `icon`, `sort_order`, `permission_code`, `menu_type`, `enabled`) VALUES (6, NULL, '菜单管理', '/admin/menus', 'MenuOutline', 4, 'system:menu:read', 'menu', 1);
