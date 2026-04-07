package cn.zhkj.xsys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MenuSaveRequest(
        Long parentId,
        @NotBlank String title,
        String path,
        String icon,
        @NotNull Integer sortOrder,
        String permissionCode,
        @NotBlank String menuType,
        @NotNull Boolean enabled,
        List<Long> visibleRoleIds) {}
