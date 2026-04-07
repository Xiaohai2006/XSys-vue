package cn.zhkj.xsys.service;

import cn.zhkj.xsys.dto.DefaultRegisterRoleSettingDto;
import cn.zhkj.xsys.dto.DefaultRegisterRoleUpdateRequest;

public interface SettingService {

    DefaultRegisterRoleSettingDto getDefaultRegisterRole();

    DefaultRegisterRoleSettingDto setDefaultRegisterRole(DefaultRegisterRoleUpdateRequest req);
}
