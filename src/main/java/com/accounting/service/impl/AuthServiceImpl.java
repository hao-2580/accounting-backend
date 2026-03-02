package com.accounting.service.impl;

import com.accounting.dto.LoginRequest;
import com.accounting.dto.LoginResponse;
import com.accounting.entity.SysUser;
import com.accounting.exception.BusinessException;
import com.accounting.mapper.SysUserMapper;
import com.accounting.service.AuthService;
import com.accounting.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (user == null) throw new BusinessException(401, "用户名或密码错误");
        if (!user.getEnabled()) throw new BusinessException(403, "账号已被禁用");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException(401, "用户名或密码错误");

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getUsername(), user.getRealName(), user.getRole());
    }
}
