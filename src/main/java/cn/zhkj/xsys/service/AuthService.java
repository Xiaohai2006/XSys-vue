package cn.zhkj.xsys.service;

import cn.zhkj.xsys.dto.LoginRequest;
import cn.zhkj.xsys.dto.RegisterRequest;
import cn.zhkj.xsys.dto.TokenResponse;
import cn.zhkj.xsys.dto.UserProfileDto;

public interface AuthService {

    TokenResponse login(LoginRequest req);

    TokenResponse register(RegisterRequest req);

    UserProfileDto me(Long userId);
}
