package cn.zhkj.xsys.dto;

import java.util.List;

public record UserProfileDto(
        Long id,
        String username,
        String nickname,
        List<String> roles,
        List<String> permissions) {}
