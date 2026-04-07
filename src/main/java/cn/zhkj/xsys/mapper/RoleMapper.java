package cn.zhkj.xsys.mapper;

import cn.zhkj.xsys.domain.Role;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {

    Role findById(@Param("id") Long id);

    Role findByCode(@Param("code") String code);

    List<Role> findAll();

    int insert(Role role);

    int update(Role role);

    int deleteById(@Param("id") Long id);

    long count();
}
