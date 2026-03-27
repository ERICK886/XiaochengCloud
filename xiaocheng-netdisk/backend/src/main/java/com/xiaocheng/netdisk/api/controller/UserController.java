package com.xiaocheng.netdisk.api.controller;

import com.xiaocheng.netdisk.api.dto.LoginDTO;
import com.xiaocheng.netdisk.api.dto.RegisterDTO;
import com.xiaocheng.netdisk.common.utils.ApiResponse;
import com.xiaocheng.netdisk.dal.entity.User;
import com.xiaocheng.netdisk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterDTO dto) {
        return ApiResponse.success(userService.register(dto));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return ApiResponse.success(userService.login(dto));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        userService.logout(userId);
        return ApiResponse.success();
    }

    @GetMapping("/info")
    public ApiResponse<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ApiResponse.success(userService.getUserInfo(userId));
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Long userId = extractUserId(token);
        userService.updatePassword(userId, oldPassword, newPassword);
        return ApiResponse.success();
    }

    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(payload);
        return jsonObject.getLong("userId");
    }
}
