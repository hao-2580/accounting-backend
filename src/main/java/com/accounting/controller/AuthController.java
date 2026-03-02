package com.accounting.controller;

import com.accounting.dto.LoginRequest;
import com.accounting.dto.LoginResponse;
import com.accounting.dto.Result;
import com.accounting.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证", description = "登录/登出")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 是无状态的，退出登录主要由前端删除 token
        // 这个接口主要用于规范化流程，可以在这里添加日志记录等
        return Result.ok();
    }
}
