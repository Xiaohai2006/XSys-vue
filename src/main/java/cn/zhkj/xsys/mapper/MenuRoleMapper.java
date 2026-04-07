package cn.zhkj.xsys.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MenuRoleMapper {

    List<Long> findRoleIdsByMenuId(@Param("menuId") Long menuId);

    void deleteByMenuId(@Param("menuId") Long menuId);

    void insert(@Param("menuId") Long menuId, @Param("roleId") Long roleId);

    void batchInsert(@Param("menuId") Long menuId, @Param("roleIds") List<Long> roleIds);
}
