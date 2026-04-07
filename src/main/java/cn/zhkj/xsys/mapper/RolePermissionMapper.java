package cn.zhkj.xsys.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper {

    int deleteByRoleId(@Param("roleId") Long roleId);

    int deleteByPermissionId(@Param("permissionId") Long permissionId);

    int insert(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
