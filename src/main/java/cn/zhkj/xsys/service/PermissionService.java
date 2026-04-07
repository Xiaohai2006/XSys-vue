package cn.zhkj.xsys.service;

import cn.zhkj.xsys.domain.Permission;
import cn.zhkj.xsys.dto.PermissionUpsertRequest;
import java.util.List;

public interface PermissionService {

    List<Permission> listAll();

    Permission findById(Long id);

    Permission create(PermissionUpsertRequest req);

    Permission update(Long id, PermissionUpsertRequest req);

    void delete(Long id);
}
