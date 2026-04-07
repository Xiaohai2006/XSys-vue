package cn.zhkj.xsys.mapper;

import cn.zhkj.xsys.domain.User;
import cn.zhkj.xsys.dto.DailyUserStatsDto;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Long id);

    int insert(User user);

    long count();

    List<User> findAll();

    List<String> findRoleCodesByUserId(@Param("userId") Long userId);

    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);

    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    List<DailyUserStatsDto> countDailyNewUsers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
