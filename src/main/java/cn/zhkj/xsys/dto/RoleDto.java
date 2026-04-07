package cn.zhkj.xsys.dto;

import java.util.List;

public record RoleDto(Long id, String code, String name, String description, List<Long> permissionIds) {}
