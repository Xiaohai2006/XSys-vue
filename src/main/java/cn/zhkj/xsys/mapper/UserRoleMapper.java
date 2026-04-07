package cn.zhkj.xsys.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper {

    int deleteByUserId(@Param("userId") Long userId);

    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    long countUsersByRoleId(@Param("roleId") Long roleId);
}
