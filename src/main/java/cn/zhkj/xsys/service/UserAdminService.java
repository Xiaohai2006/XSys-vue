package cn.zhkj.xsys.service;

import cn.zhkj.xsys.dto.AssignUserRolesRequest;
import cn.zhkj.xsys.dto.DailyUserStatsDto;
import cn.zhkj.xsys.dto.UserAdminDto;
import java.util.List;

public interface UserAdminService {

    List<UserAdminDto> listUsers();

    UserAdminDto assignRoles(Long userId, AssignUserRolesRequest req);

    List<DailyUserStatsDto> getDailyNewUserStats(int days);
}
