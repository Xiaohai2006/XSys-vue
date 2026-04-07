package cn.zhkj.xsys.controller;

import cn.zhkj.xsys.api.ApiResponse;
import cn.zhkj.xsys.dto.AssignPermissionsRequest;
import cn.zhkj.xsys.dto.RoleDto;
import cn.zhkj.xsys.dto.RoleUpsertRequest;
import cn.zhkj.xsys.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

    private final RoleService roleService;

    public AdminRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:read')")
    public ApiResponse<List<RoleDto>> list() {
        return ApiResponse.ok(roleService.listRoles());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<RoleDto> create(@Valid @RequestBody RoleUpsertRequest request) {
        return ApiResponse.ok(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<RoleDto> update(
            @PathVariable("id") Long id, @Valid @RequestBody RoleUpsertRequest request) {
        return ApiResponse.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<RoleDto> assignPermissions(
            @PathVariable("id") Long id, @RequestBody AssignPermissionsRequest request) {
        return ApiResponse.ok(roleService.assignPermissions(id, request));
    }
}
