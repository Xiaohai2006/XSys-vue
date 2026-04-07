package cn.zhkj.xsys.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserAdminDto(
        Long id,
        String username,
        String nickname,
        Boolean enabled,
        LocalDateTime createdAt,
        List<String> roleCodes) {}
