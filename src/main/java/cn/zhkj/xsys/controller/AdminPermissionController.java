package cn.zhkj.xsys.controller;

import cn.zhkj.xsys.api.ApiResponse;
import cn.zhkj.xsys.domain.Permission;
import cn.zhkj.xsys.dto.PermissionUpsertRequest;
import cn.zhkj.xsys.service.PermissionService;
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
@RequestMapping("/api/admin/permissions")
public class AdminPermissionController {

    private final PermissionService permissionService;

    public AdminPermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:read')")
    public ApiResponse<List<Permission>> list() {
        return ApiResponse.ok(permissionService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:read')")
    public ApiResponse<Permission> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(permissionService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<Permission> create(@Valid @RequestBody PermissionUpsertRequest request) {
        return ApiResponse.ok(permissionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<Permission> update(
            @PathVariable("id") Long id, @Valid @RequestBody PermissionUpsertRequest request) {
        return ApiResponse.ok(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:write')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        permissionService.delete(id);
        return ApiResponse.ok();
    }
}
