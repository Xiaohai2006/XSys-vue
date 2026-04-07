package cn.zhkj.xsys.service;

import cn.zhkj.xsys.domain.Permission;
import cn.zhkj.xsys.dto.PermissionUpsertRequest;
import cn.zhkj.xsys.mapper.PermissionMapper;
import cn.zhkj.xsys.mapper.RolePermissionMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public PermissionService(PermissionMapper permissionMapper, RolePermissionMapper rolePermissionMapper) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    public List<Permission> listAll() {
        return permissionMapper.findAll();
    }

    public Permission findById(Long id) {
        return permissionMapper.findById(id);
    }

    @Transactional
    public Permission create(PermissionUpsertRequest req) {
        if (permissionMapper.findByCode(req.code()) != null) {
            throw new IllegalArgumentException("权限编码已存在: " + req.code());
        }
        Permission p = new Permission();
        p.setCode(req.code().trim());
        p.setName(req.name().trim());
        p.setDescription(req.description() != null ? req.description().trim() : null);
        permissionMapper.insert(p);
        return p;
    }

    @Transactional
    public Permission update(Long id, PermissionUpsertRequest req) {
        Permission existing = permissionMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("权限不存在");
        }
        Permission byCode = permissionMapper.findByCode(req.code());
        if (byCode != null && !byCode.getId().equals(id)) {
            throw new IllegalArgumentException("权限编码已存在: " + req.code());
        }
        Permission p = new Permission();
        p.setId(id);
        p.setCode(req.code().trim());
        p.setName(req.name().trim());
        p.setDescription(req.description() != null ? req.description().trim() : null);
        permissionMapper.update(p);
        return permissionMapper.findById(id);
    }

    @Transactional
    public void delete(Long id) {
        if (permissionMapper.findById(id) == null) {
            throw new IllegalArgumentException("权限不存在");
        }
        rolePermissionMapper.deleteByPermissionId(id);
        permissionMapper.deleteById(id);
    }
}
