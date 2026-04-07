package cn.zhkj.xsys.mapper;

import cn.zhkj.xsys.domain.Permission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PermissionMapper {

    Permission findById(@Param("id") Long id);

    Permission findByCode(@Param("code") String code);

    List<Permission> findAll();

    int insert(Permission permission);

    int update(Permission permission);

    int deleteById(@Param("id") Long id);

    long count();
}
