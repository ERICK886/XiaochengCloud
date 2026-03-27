package com.xiaocheng.netdisk.controller;

import com.xiaocheng.netdisk.dto.ApiResponse;
import com.xiaocheng.netdisk.dto.LoginRequest;
import com.xiaocheng.netdisk.entity.User;
import com.xiaocheng.netdisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam(required = false) String phone) {
        User user = userService.register(username, password, phone);
        return ApiResponse.success(user);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(token);
    }

    @GetMapping("/info")
    public ApiResponse<User> getUserInfo(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        User user = userService.getUserInfo(userId);
        return ApiResponse.success(user);
    }

    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(Authentication authentication,
                                             @RequestParam String oldPassword,
                                             @RequestParam String newPassword) {
        Long userId = Long.parseLong(authentication.getName());
        userService.updatePassword(userId, oldPassword, newPassword);
        return ApiResponse.success();
    }
}
