package cn.zhkj.xsys.controller;

import cn.zhkj.xsys.api.ApiResponse;
import cn.zhkj.xsys.dto.MenuAdminDto;
import cn.zhkj.xsys.dto.MenuSaveRequest;
import cn.zhkj.xsys.dto.MenuSortRequest;
import cn.zhkj.xsys.service.MenuService;
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
@RequestMapping("/api/admin/menus")
public class AdminMenuController {

    private final MenuService menuService;

    public AdminMenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:menu:read')")
    public ApiResponse<List<MenuAdminDto>> tree() {
        return ApiResponse.ok(menuService.adminTree());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:write')")
    public ApiResponse<MenuAdminDto> create(@Valid @RequestBody MenuSaveRequest request) {
        return ApiResponse.ok(menuService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:write')")
    public ApiResponse<MenuAdminDto> update(
            @PathVariable("id") Long id, @Valid @RequestBody MenuSaveRequest request) {
        return ApiResponse.ok(menuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:write')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        menuService.delete(id);
        return ApiResponse.ok();
    }

    @PutMapping("/sort")
    @PreAuthorize("hasAuthority('system:menu:write')")
    public ApiResponse<Void> sort(@RequestBody MenuSortRequest request) {
        menuService.updateSort(request);
        return ApiResponse.ok();
    }
}
