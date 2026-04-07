package cn.zhkj.xsys.service.impl;

import cn.zhkj.xsys.config.SysKeys;
import cn.zhkj.xsys.domain.User;
import cn.zhkj.xsys.dto.LoginRequest;
import cn.zhkj.xsys.dto.RegisterRequest;
import cn.zhkj.xsys.dto.TokenResponse;
import cn.zhkj.xsys.dto.UserProfileDto;
import cn.zhkj.xsys.mapper.RoleMapper;
import cn.zhkj.xsys.mapper.SettingMapper;
import cn.zhkj.xsys.mapper.UserMapper;
import cn.zhkj.xsys.mapper.UserRoleMapper;
import cn.zhkj.xsys.security.JwtUtil;
import cn.zhkj.xsys.service.AuthService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final SettingMapper settingMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(
            UserMapper userMapper,
            UserRoleMapper userRoleMapper,
            RoleMapper roleMapper,
            SettingMapper settingMapper,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.settingMapper = settingMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public TokenResponse login(LoginRequest req) {
        User user = userMapper.findByUsername(req.username());
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("用户名或密码错误");
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BadCredentialsException("账号已禁用");
        }
        List<String> roles = userMapper.findRoleCodesByUserId(user.getId());
        List<String> perms = userMapper.findPermissionCodesByUserId(user.getId());
        String token =
                jwtUtil.generate(user.getId(), user.getUsername(), roles, perms);
        UserProfileDto profile = buildProfile(user);
        return TokenResponse.of(token, jwtUtil.getExpirationSeconds(), profile);
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest req) {
        if (userMapper.findByUsername(req.username()) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }
        Long defaultRoleId = resolveDefaultRegisterRoleId();
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname() != null && !req.nickname().isBlank() ? req.nickname() : req.username());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        userRoleMapper.insert(user.getId(), defaultRoleId);
        List<String> roles = userMapper.findRoleCodesByUserId(user.getId());
        List<String> perms = userMapper.findPermissionCodesByUserId(user.getId());
        String token =
                jwtUtil.generate(user.getId(), user.getUsername(), roles, perms);
        UserProfileDto profile = buildProfile(user);
        return TokenResponse.of(token, jwtUtil.getExpirationSeconds(), profile);
    }

    @Override
    public UserProfileDto me(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return buildProfile(user);
    }

    private UserProfileDto buildProfile(User user) {
        List<String> roles = userMapper.findRoleCodesByUserId(user.getId());
        List<String> perms = userMapper.findPermissionCodesByUserId(user.getId());
        return new UserProfileDto(user.getId(), user.getUsername(), user.getNickname(), roles, perms);
    }

    private Long resolveDefaultRegisterRoleId() {
        String v = settingMapper.getValue(SysKeys.DEFAULT_REGISTER_ROLE_ID);
        if (v != null && !v.isBlank()) {
            try {
                Long id = Long.parseLong(v.trim());
                if (roleMapper.findById(id) != null) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        var userRole = roleMapper.findByCode(SysKeys.ROLE_USER);
        if (userRole == null) {
            throw new IllegalStateException("未配置默认注册角色，且不存在 USER 角色");
        }
        return userRole.getId();
    }
}
