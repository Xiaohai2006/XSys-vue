package cn.zhkj.xsys.service;

import cn.zhkj.xsys.dto.AssignPermissionsRequest;
import cn.zhkj.xsys.dto.RoleDto;
import cn.zhkj.xsys.dto.RoleUpsertRequest;
import java.util.List;

public interface RoleService {

    List<RoleDto> listRoles();

    RoleDto createRole(RoleUpsertRequest req);

    RoleDto updateRole(Long id, RoleUpsertRequest req);

    void deleteRole(Long id);

    RoleDto assignPermissions(Long roleId, AssignPermissionsRequest req);
}
