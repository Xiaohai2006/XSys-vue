package cn.zhkj.xsys.service.impl;

import cn.zhkj.xsys.config.SysKeys;
import cn.zhkj.xsys.domain.Role;
import cn.zhkj.xsys.dto.DefaultRegisterRoleSettingDto;
import cn.zhkj.xsys.dto.DefaultRegisterRoleUpdateRequest;
import cn.zhkj.xsys.mapper.RoleMapper;
import cn.zhkj.xsys.mapper.SettingMapper;
import cn.zhkj.xsys.service.SettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingServiceImpl implements SettingService {

    private final SettingMapper settingMapper;
    private final RoleMapper roleMapper;

    public SettingServiceImpl(SettingMapper settingMapper, RoleMapper roleMapper) {
        this.settingMapper = settingMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public DefaultRegisterRoleSettingDto getDefaultRegisterRole() {
        String v = settingMapper.getValue(SysKeys.DEFAULT_REGISTER_ROLE_ID);
        Long roleId = null;
        if (v != null && !v.isBlank()) {
            try {
                roleId = Long.parseLong(v.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        if (roleId == null) {
            Role user = roleMapper.findByCode(SysKeys.ROLE_USER);
            if (user == null) {
                return new DefaultRegisterRoleSettingDto(null, null, null);
            }
            return new DefaultRegisterRoleSettingDto(user.getId(), user.getCode(), user.getName());
        }
        Role role = roleMapper.findById(roleId);
        if (role == null) {
            return new DefaultRegisterRoleSettingDto(null, null, null);
        }
        return new DefaultRegisterRoleSettingDto(role.getId(), role.getCode(), role.getName());
    }

    @Override
    @Transactional
    public DefaultRegisterRoleSettingDto setDefaultRegisterRole(DefaultRegisterRoleUpdateRequest req) {
        Role role = roleMapper.findById(req.roleId());
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        settingMapper.upsert(SysKeys.DEFAULT_REGISTER_ROLE_ID, String.valueOf(role.getId()));
        return new DefaultRegisterRoleSettingDto(role.getId(), role.getCode(), role.getName());
    }
}
