package cn.zhkj.xsys.service.impl;

import cn.zhkj.xsys.domain.User;
import cn.zhkj.xsys.dto.AssignUserRolesRequest;
import cn.zhkj.xsys.dto.DailyUserStatsDto;
import cn.zhkj.xsys.dto.UserAdminDto;
import cn.zhkj.xsys.mapper.RoleMapper;
import cn.zhkj.xsys.mapper.UserMapper;
import cn.zhkj.xsys.mapper.UserRoleMapper;
import cn.zhkj.xsys.service.UserAdminService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public UserAdminServiceImpl(
            UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<UserAdminDto> listUsers() {
        return userMapper.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public UserAdminDto assignRoles(Long userId, AssignUserRolesRequest req) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        List<Long> roleIds = req.roleIds() == null ? List.of() : req.roleIds();
        for (Long rid : roleIds) {
            if (roleMapper.findById(rid) == null) {
                throw new IllegalArgumentException("角色不存在: " + rid);
            }
        }
        userRoleMapper.deleteByUserId(userId);
        for (Long rid : roleIds) {
            userRoleMapper.insert(userId, rid);
        }
        return toDto(userMapper.findById(userId));
    }

    private UserAdminDto toDto(User user) {
        List<String> codes =
                userRoleMapper.findRoleIdsByUserId(user.getId()).stream()
                        .map(roleMapper::findById)
                        .filter(r -> r != null)
                        .map(r -> r.getCode())
                        .toList();
        return new UserAdminDto(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEnabled(),
                user.getCreatedAt(),
                codes);
    }

    @Override
    public List<DailyUserStatsDto> getDailyNewUserStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        List<DailyUserStatsDto> stats = userMapper.countDailyNewUsers(startDate, endDate);
        Map<LocalDate, Long> statsMap = stats.stream()
                .collect(Collectors.toMap(DailyUserStatsDto::date, DailyUserStatsDto::count));
        List<DailyUserStatsDto> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            result.add(new DailyUserStatsDto(date, statsMap.getOrDefault(date, 0L)));
        }
        return result;
    }
}
