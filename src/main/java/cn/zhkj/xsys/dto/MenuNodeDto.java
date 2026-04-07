package cn.zhkj.xsys.dto;

import java.util.List;

public record MenuNodeDto(
        Long id, 
        String title, 
        String path, 
        String icon, 
        String menuType, 
        List<Long> visibleRoleIds,
        List<MenuNodeDto> children) {}
