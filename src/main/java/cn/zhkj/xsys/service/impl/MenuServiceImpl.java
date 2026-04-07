package cn.zhkj.xsys.service.impl;

import cn.zhkj.xsys.domain.Menu;
import cn.zhkj.xsys.dto.MenuAdminDto;
import cn.zhkj.xsys.dto.MenuNodeDto;
import cn.zhkj.xsys.dto.MenuSaveRequest;
import cn.zhkj.xsys.dto.MenuSortRequest;
import cn.zhkj.xsys.mapper.MenuMapper;
import cn.zhkj.xsys.mapper.MenuRoleMapper;
import cn.zhkj.xsys.mapper.UserMapper;
import cn.zhkj.xsys.service.MenuService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final MenuRoleMapper menuRoleMapper;
    private final UserMapper userMapper;

    public MenuServiceImpl(MenuMapper menuMapper, MenuRoleMapper menuRoleMapper, UserMapper userMapper) {
        this.menuMapper = menuMapper;
        this.menuRoleMapper = menuRoleMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<MenuAdminDto> adminTree() {
        List<Menu> rows = menuMapper.findAllOrdered();
        Map<Long, List<Long>> roleMap = loadMenuRoleMap(rows);
        return buildAdminTree(rows, roleMap);
    }

    private Map<Long, List<Long>> loadMenuRoleMap(List<Menu> menus) {
        Map<Long, List<Long>> map = new LinkedHashMap<>();
        for (Menu m : menus) {
            map.put(m.getId(), menuRoleMapper.findRoleIdsByMenuId(m.getId()));
        }
        return map;
    }

    @Override
    @Transactional
    public MenuAdminDto create(MenuSaveRequest req) {
        validateMenuType(req.menuType());
        Menu m = toEntity(null, req);
        resolveParent(m);
        menuMapper.insert(m);
        syncMenuRoles(m.getId(), req.visibleRoleIds());
        return findAdminById(m.getId());
    }

    @Override
    @Transactional
    public MenuAdminDto update(Long id, MenuSaveRequest req) {
        Menu existing = menuMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("菜单不存在");
        }
        validateMenuType(req.menuType());
        Menu m = toEntity(id, req);
        resolveParent(m);
        if (m.getParentId() != null && Objects.equals(m.getParentId(), id)) {
            throw new IllegalArgumentException("不能将父级设为自身");
        }
        if (m.getParentId() != null && isDescendant(id, m.getParentId())) {
            throw new IllegalArgumentException("不能将父级设为子节点");
        }
        menuMapper.update(m);
        syncMenuRoles(id, req.visibleRoleIds());
        return findAdminById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (menuMapper.findById(id) == null) {
            throw new IllegalArgumentException("菜单不存在");
        }
        menuRoleMapper.deleteByMenuId(id);
        menuMapper.deleteById(id);
    }

    private void syncMenuRoles(Long menuId, List<Long> roleIds) {
        menuRoleMapper.deleteByMenuId(menuId);
        if (roleIds != null && !roleIds.isEmpty()) {
            menuRoleMapper.batchInsert(menuId, roleIds);
        }
    }

    @Override
    @Transactional
    public void updateSort(MenuSortRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            return;
        }
        for (MenuSortRequest.MenuItem item : request.items()) {
            if (item.id() == null) continue;
            menuMapper.updateSortOrder(item.id(), item.parentId(), item.sortOrder());
        }
    }

    @Override
    public List<MenuNodeDto> treeForUser(Long userId) {
        List<String> perms = userMapper.findPermissionCodesByUserId(userId);
        Set<String> permSet = perms.stream().collect(Collectors.toSet());
        List<Long> userRoleIds = userMapper.findRoleIdsByUserId(userId);
        Set<Long> userRoleIdSet = new HashSet<>(userRoleIds);
        
        List<Menu> allMenus = menuMapper.findAllOrdered();
        Map<Long, List<Long>> menuRoleMap = loadMenuRoleMap(allMenus);
        
        List<Menu> visible = allMenus.stream()
                .filter(m -> Boolean.TRUE.equals(m.getEnabled()))
                .filter(m -> isMenuVisible(m, permSet, menuRoleMap.getOrDefault(m.getId(), List.of()), userRoleIdSet))
                .toList();
        
        List<MenuNodeDto> tree = buildNodeTree(visible, menuRoleMap);
        return pruneEmptyCatalogs(tree);
    }

    private boolean isMenuVisible(Menu m, Set<String> permSet, List<Long> menuRoleIds, Set<Long> userRoleIds) {
        if (m.getPermissionCode() != null && !m.getPermissionCode().isBlank()) {
            if (!permSet.contains(m.getPermissionCode())) {
                return false;
            }
        }
        if (menuRoleIds == null || menuRoleIds.isEmpty()) {
            return true;
        }
        return menuRoleIds.stream().anyMatch(userRoleIds::contains);
    }

    private MenuAdminDto findAdminById(Long id) {
        List<MenuAdminDto> tree = adminTree();
        return findInAdminTree(tree, id);
    }

    private MenuAdminDto findInAdminTree(List<MenuAdminDto> nodes, Long id) {
        for (MenuAdminDto n : nodes) {
            if (Objects.equals(n.id(), id)) {
                return n;
            }
            if (n.children() != null) {
                MenuAdminDto f = findInAdminTree(n.children(), id);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    private boolean isDescendant(Long ancestorId, Long candidateParentId) {
        Long p = candidateParentId;
        int guard = 0;
        while (p != null && guard++ < 64) {
            if (Objects.equals(p, ancestorId)) {
                return true;
            }
            Menu row = menuMapper.findById(p);
            p = row != null ? row.getParentId() : null;
        }
        return false;
    }

    private void resolveParent(Menu m) {
        if (m.getParentId() == null) {
            return;
        }
        if (menuMapper.findById(m.getParentId()) == null) {
            throw new IllegalArgumentException("父级菜单不存在");
        }
    }

    private static void validateMenuType(String t) {
        if (!"menu".equals(t) && !"catalog".equals(t)) {
            throw new IllegalArgumentException("menuType 仅支持 menu 或 catalog");
        }
    }

    private static Menu toEntity(Long id, MenuSaveRequest req) {
        Menu m = new Menu();
        m.setId(id);
        m.setParentId(req.parentId());
        m.setTitle(req.title().trim());
        m.setPath(req.path() == null ? "" : req.path().trim());
        m.setIcon(req.icon() != null && !req.icon().isBlank() ? req.icon().trim() : null);
        m.setSortOrder(req.sortOrder());
        m.setPermissionCode(
                req.permissionCode() != null && !req.permissionCode().isBlank()
                        ? req.permissionCode().trim()
                        : null);
        m.setMenuType(req.menuType());
        m.setEnabled(req.enabled());
        return m;
    }

    private List<MenuAdminDto> buildAdminTree(List<Menu> rows, Map<Long, List<Long>> roleMap) {
        Map<Long, List<Menu>> byParent = new LinkedHashMap<>();
        for (Menu m : rows) {
            Long pk = m.getParentId() == null ? Long.valueOf(0L) : m.getParentId();
            byParent.computeIfAbsent(pk, k -> new ArrayList<>()).add(m);
        }
        return buildAdminChildren(0L, byParent, roleMap);
    }

    private List<MenuAdminDto> buildAdminChildren(Long parentKey, Map<Long, List<Menu>> byParent, Map<Long, List<Long>> roleMap) {
        List<Menu> level = byParent.getOrDefault(parentKey, List.of());
        List<MenuAdminDto> out = new ArrayList<>();
        for (Menu m : level) {
            List<MenuAdminDto> ch = buildAdminChildren(m.getId(), byParent, roleMap);
            out.add(
                    new MenuAdminDto(
                            m.getId(),
                            m.getParentId(),
                            m.getTitle(),
                            m.getPath(),
                            m.getIcon(),
                            m.getSortOrder(),
                            m.getPermissionCode(),
                            m.getMenuType(),
                            m.getEnabled(),
                            roleMap.getOrDefault(m.getId(), List.of()),
                            ch.isEmpty() ? null : ch));
        }
        return out;
    }

    private List<MenuNodeDto> buildNodeTree(List<Menu> rows, Map<Long, List<Long>> roleMap) {
        Map<Long, List<Menu>> byParent = new LinkedHashMap<>();
        for (Menu m : rows) {
            Long pk = m.getParentId() == null ? Long.valueOf(0L) : m.getParentId();
            byParent.computeIfAbsent(pk, k -> new ArrayList<>()).add(m);
        }
        return buildNodeChildren(0L, byParent, roleMap);
    }

    private List<MenuNodeDto> buildNodeChildren(Long parentKey, Map<Long, List<Menu>> byParent, Map<Long, List<Long>> roleMap) {
        List<Menu> level = byParent.getOrDefault(parentKey, List.of());
        List<MenuNodeDto> out = new ArrayList<>();
        for (Menu m : level) {
            List<MenuNodeDto> ch = buildNodeChildren(m.getId(), byParent, roleMap);
            out.add(
                    new MenuNodeDto(
                            m.getId(),
                            m.getTitle(),
                            m.getPath(),
                            m.getIcon(),
                            m.getMenuType(),
                            roleMap.getOrDefault(m.getId(), List.of()),
                            ch.isEmpty() ? null : ch));
        }
        return out;
    }

    private List<MenuNodeDto> pruneEmptyCatalogs(List<MenuNodeDto> nodes) {
        List<MenuNodeDto> out = new ArrayList<>();
        for (MenuNodeDto n : nodes) {
            List<MenuNodeDto> ch = n.children() == null ? List.of() : pruneEmptyCatalogs(n.children());
            if ("catalog".equals(n.menuType()) && ch.isEmpty()) {
                continue;
            }
            out.add(
                    new MenuNodeDto(
                            n.id(),
                            n.title(),
                            n.path(),
                            n.icon(),
                            n.menuType(),
                            n.visibleRoleIds(),
                            ch.isEmpty() ? null : ch));
        }
        return out;
    }
}
