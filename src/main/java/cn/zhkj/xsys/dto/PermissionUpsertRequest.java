package cn.zhkj.xsys.dto;

import jakarta.validation.constraints.NotBlank;

public record PermissionUpsertRequest(
    @NotBlank(message = "权限编码不能为空") String code,
    @NotBlank(message = "权限名称不能为空") String name,
    String description
) {}
