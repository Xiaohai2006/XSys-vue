package cn.zhkj.xsys.config;

import cn.zhkj.xsys.domain.Menu;
import cn.zhkj.xsys.domain.Permission;
import cn.zhkj.xsys.domain.Role;
import cn.zhkj.xsys.domain.User;
import cn.zhkj.xsys.mapper.MenuMapper;
import cn.zhkj.xsys.mapper.PermissionMapper;
import cn.zhkj.xsys.mapper.RoleMapper;
import cn.zhkj.xsys.mapper.RolePermissionMapper;
import cn.zhkj.xsys.mapper.SettingMapper;
import cn.zhkj.xsys.mapper.UserMapper;
import cn.zhkj.xsys.mapper.UserRoleMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(10)
public class DatabaseSeeder implements ApplicationRunner {

    private final PermissionMapper permissionMapper;
    private final MenuMapper menuMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final SettingMapper settingMapper;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            PermissionMapper permissionMapper,
            MenuMapper menuMapper,
            RoleMapper roleMapper,
            RolePermissionMapper rolePermissionMapper,
            UserMapper userMapper,
            UserRoleMapper userRoleMapper,
            SettingMapper settingMapper,
            PasswordEncoder passwordEncoder) {
        this.permissionMapper = permissionMapper;
        this.menuMapper = menuMapper;
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.settingMapper = settingMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (permissionMapper.count() == 0) {
            seedPermissions();
        }
        if (roleMapper.count() == 0) {
            seedRolesAndBindings();
        }
        if (userMapper.count() == 0) {
            seedAdminUser();
        }
        if (menuMapper.count() == 0) {
            seedMenus();
        }
        ensureDefaultRegisterRoleSetting();
    }

    private void seedPermissions() {
        String[][] defs = {
            {"system:user:read", "查看用户", "用户列表"},
            {"system:user:write", "管理用户角色", "分配用户角色"},
            {"system:role:read", "查看角色权限", "角色与权限列表"},
            {"system:role:write", "管理角色权限", "增删改角色及权限绑定"},
            {"system:settings:read", "查看系统设置", "默认注册角色等"},
            {"system:settings:write", "修改系统设置", "默认注册角色等"},
            {"system:menu:read", "查看菜单", "侧边栏与菜单管理页"},
            {"system:menu:write", "管理菜单", "增删改菜单"},
        };
        for (String[] d : defs) {
            Permission p = new Permission();
            p.setCode(d[0]);
            p.setName(d[1]);
            p.setDescription(d[2]);
            permissionMapper.insert(p);
        }
    }

    private void seedRolesAndBindings() {
        Role admin = new Role();
        admin.setCode(SysKeys.ROLE_ADMIN);
        admin.setName("管理员");
        admin.setDescription("内置管理员");
        roleMapper.insert(admin);

        Role user = new Role();
        user.setCode(SysKeys.ROLE_USER);
        user.setName("普通用户");
        user.setDescription("默认注册用户");
        roleMapper.insert(user);

        List<Permission> all = permissionMapper.findAll();
        for (Permission p : all) {
            rolePermissionMapper.insert(admin.getId(), p.getId());
        }
    }

    private void seedMenus() {
        Object[][] defs = {
            {null, "仪表盘", "/admin", 0, null, "menu", true},
            {null, "用户", "/admin/users", 10, "system:user:read", "menu", true},
            {null, "角色管理", "/admin/roles", 20, "system:role:read", "menu", true},
            {null, "权限管理", "/admin/permissions", 25, "system:role:read", "menu", true},
            {null, "系统设置", "/admin/settings", 30, "system:settings:read", "menu", true},
            {null, "菜单管理", "/admin/menus", 40, "system:menu:read", "menu", true},
        };
        for (Object[] d : defs) {
            Menu m = new Menu();
            m.setParentId((Long) d[0]);
            m.setTitle((String) d[1]);
            m.setPath((String) d[2]);
            m.setIcon(null);
            m.setSortOrder((Integer) d[3]);
            m.setPermissionCode((String) d[4]);
            m.setMenuType((String) d[5]);
            m.setEnabled((Boolean) d[6]);
            menuMapper.insert(m);
        }
    }

    private void seedAdminUser() {
        Role adminRole = roleMapper.findByCode(SysKeys.ROLE_ADMIN);
        if (adminRole == null) {
            return;
        }
        if (userMapper.findByUsername("admin") != null) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setNickname("管理员");
        admin.setEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        userMapper.insert(admin);
        userRoleMapper.insert(admin.getId(), adminRole.getId());
    }

    private void ensureDefaultRegisterRoleSetting() {
        if (settingMapper.getValue(SysKeys.DEFAULT_REGISTER_ROLE_ID) != null) {
            return;
        }
        Role user = roleMapper.findByCode(SysKeys.ROLE_USER);
        if (user != null) {
            settingMapper.upsert(SysKeys.DEFAULT_REGISTER_ROLE_ID, String.valueOf(user.getId()));
        }
    }
}
