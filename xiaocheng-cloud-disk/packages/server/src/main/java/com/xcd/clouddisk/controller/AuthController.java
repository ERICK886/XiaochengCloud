package com.xcd.clouddisk.controller;

import com.xcd.clouddisk.dto.ApiResponse;
import com.xcd.clouddisk.dto.AuthResponse;
import com.xcd.clouddisk.dto.LoginRequest;
import com.xcd.clouddisk.dto.RegisterRequest;
import com.xcd.clouddisk.dto.UserDTO;
import com.xcd.clouddisk.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
    
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }
    
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(Authentication authentication) {
        return authService.getCurrentUser((String) authentication.getPrincipal());
    }
    
    @GetMapping("/storage")
    public ApiResponse<AuthService.StorageInfo> getStorageInfo(Authentication authentication) {
        return authService.getStorageInfo((String) authentication.getPrincipal());
    }
    
    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        return ApiResponse.error("功能开发中");
    }
}
