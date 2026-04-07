package cn.zhkj.xsys.dto;

import jakarta.validation.constraints.NotNull;

public record DefaultRegisterRoleUpdateRequest(@NotNull(message = "roleId 不能为空") Long roleId) {}
