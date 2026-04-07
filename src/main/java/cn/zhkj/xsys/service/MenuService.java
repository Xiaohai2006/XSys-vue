package cn.zhkj.xsys.service;

import cn.zhkj.xsys.dto.MenuAdminDto;
import cn.zhkj.xsys.dto.MenuNodeDto;
import cn.zhkj.xsys.dto.MenuSaveRequest;
import cn.zhkj.xsys.dto.MenuSortRequest;
import java.util.List;

public interface MenuService {

    List<MenuAdminDto> adminTree();

    MenuAdminDto create(MenuSaveRequest req);

    MenuAdminDto update(Long id, MenuSaveRequest req);

    void delete(Long id);

    void updateSort(MenuSortRequest request);

    List<MenuNodeDto> treeForUser(Long userId);
}
