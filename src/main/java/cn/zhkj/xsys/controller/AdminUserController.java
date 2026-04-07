package cn.zhkj.xsys.controller;

import cn.zhkj.xsys.api.ApiResponse;
import cn.zhkj.xsys.dto.AssignUserRolesRequest;
import cn.zhkj.xsys.dto.DailyUserStatsDto;
import cn.zhkj.xsys.dto.UserAdminDto;
import cn.zhkj.xsys.service.UserAdminService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:user:read')")
    public ApiResponse<List<UserAdminDto>> list() {
        return ApiResponse.ok(userAdminService.listUsers());
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:write')")
    public ApiResponse<UserAdminDto> assignRoles(
            @PathVariable("id") Long id, @RequestBody AssignUserRolesRequest request) {
        return ApiResponse.ok(userAdminService.assignRoles(id, request));
    }

    @GetMapping("/stats/daily-new")
    @PreAuthorize("hasAuthority('system:user:read')")
    public ApiResponse<List<DailyUserStatsDto>> getDailyNewUserStats(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.ok(userAdminService.getDailyNewUserStats(days));
    }
}
