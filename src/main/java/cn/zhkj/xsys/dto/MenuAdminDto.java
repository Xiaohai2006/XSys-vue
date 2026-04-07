package cn.zhkj.xsys.dto;

import java.util.List;

public record MenuAdminDto(
        Long id,
        Long parentId,
        String title,
        String path,
        String icon,
        Integer sortOrder,
        String permissionCode,
        String menuType,
        Boolean enabled,
        List<Long> visibleRoleIds,
        List<MenuAdminDto> children) {}
