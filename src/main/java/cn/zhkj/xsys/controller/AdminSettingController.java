package cn.zhkj.xsys.controller;

import cn.zhkj.xsys.api.ApiResponse;
import cn.zhkj.xsys.dto.DefaultRegisterRoleSettingDto;
import cn.zhkj.xsys.dto.DefaultRegisterRoleUpdateRequest;
import cn.zhkj.xsys.service.SettingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingController {

    private final SettingService settingService;

    public AdminSettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/default-register-role")
    @PreAuthorize("hasAuthority('system:settings:read')")
    public ApiResponse<DefaultRegisterRoleSettingDto> getDefaultRegisterRole() {
        return ApiResponse.ok(settingService.getDefaultRegisterRole());
    }

    @PutMapping("/default-register-role")
    @PreAuthorize("hasAuthority('system:settings:write')")
    public ApiResponse<DefaultRegisterRoleSettingDto> setDefaultRegisterRole(
            @Valid @RequestBody DefaultRegisterRoleUpdateRequest request) {
        return ApiResponse.ok(settingService.setDefaultRegisterRole(request));
    }
}
