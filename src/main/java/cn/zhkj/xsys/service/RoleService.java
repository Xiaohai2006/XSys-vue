package cn.zhkj.xsys.service;

import cn.zhkj.xsys.config.SysKeys;
import cn.zhkj.xsys.domain.Permission;
import cn.zhkj.xsys.domain.Role;
import cn.zhkj.xsys.dto.AssignPermissionsRequest;
import cn.zhkj.xsys.dto.RoleDto;
import cn.zhkj.xsys.dto.RoleUpsertRequest;
import cn.zhkj.xsys.mapper.PermissionMapper;
import cn.zhkj.xsys.mapper.RoleMapper;
import cn.zhkj.xsys.mapper.RolePermissionMapper;
import cn.zhkj.xsys.mapper.UserRoleMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleService(
            RoleMapper roleMapper,
            PermissionMapper permissionMapper,
            RolePermissionMapper rolePermissionMapper,
            UserRoleMapper userRoleMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userRoleMapper = userRoleMapper;
    }

    public List<RoleDto> listRoles() {
        return roleMapper.findAll().stream()
                .map(
                        r ->
                                new RoleDto(
                                        r.getId(),
                                        r.getCode(),
                                        r.getName(),
                                        r.getDescription(),
                                        rolePermissionMapper.findPermissionIdsByRoleId(r.getId())))
                .toList();
    }

    @Transactional
    public RoleDto createRole(RoleUpsertRequest req) {
        if (roleMapper.findByCode(req.code()) != null) {
            throw new IllegalArgumentException("角色编码已存在");
        }
        Role role = new Role();
        role.setCode(req.code());
        role.setName(req.name());
        role.setDescription(req.description());
        roleMapper.insert(role);
        return new RoleDto(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                List.of());
    }

    @Transactional
    public RoleDto updateRole(Long id, RoleUpsertRequest req) {
        Role existing = roleMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        if (SysKeys.ROLE_ADMIN.equals(existing.getCode()) && !SysKeys.ROLE_ADMIN.equals(req.code())) {
            throw new IllegalArgumentException("不能修改内置管理员角色编码");
        }
        if (!existing.getCode().equals(req.code())) {
            if (roleMapper.findByCode(req.code()) != null) {
                throw new IllegalArgumentException("角色编码已存在");
            }
        }
        existing.setCode(req.code());
        existing.setName(req.name());
        existing.setDescription(req.description());
        roleMapper.update(existing);
        return new RoleDto(
                existing.getId(),
                existing.getCode(),
                existing.getName(),
                existing.getDescription(),
                rolePermissionMapper.findPermissionIdsByRoleId(existing.getId()));
    }

    @Transactional
    public void deleteRole(Long id) {
        Role existing = roleMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        if (SysKeys.ROLE_ADMIN.equals(existing.getCode())) {
            throw new IllegalArgumentException("不能删除内置管理员角色");
        }
        if (userRoleMapper.countUsersByRoleId(id) > 0) {
            throw new IllegalArgumentException("该角色仍分配给用户，无法删除");
        }
        roleMapper.deleteById(id);
    }

    @Transactional
    public RoleDto assignPermissions(Long roleId, AssignPermissionsRequest req) {
        Role existing = roleMapper.findById(roleId);
        if (existing == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        List<Long> ids = req.permissionIds() == null ? List.of() : req.permissionIds();
        for (Long pid : ids) {
            Permission p = permissionMapper.findById(pid);
            if (p == null) {
                throw new IllegalArgumentException("权限不存在: " + pid);
            }
        }
        rolePermissionMapper.deleteByRoleId(roleId);
        for (Long pid : ids) {
            rolePermissionMapper.insert(roleId, pid);
        }
        Role updated = roleMapper.findById(roleId);
        return new RoleDto(
                updated.getId(),
                updated.getCode(),
                updated.getName(),
                updated.getDescription(),
                rolePermissionMapper.findPermissionIdsByRoleId(roleId));
    }
}
