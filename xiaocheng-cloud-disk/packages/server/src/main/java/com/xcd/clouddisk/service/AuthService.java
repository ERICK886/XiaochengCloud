package com.xcd.clouddisk.service;

import com.xcd.clouddisk.dto.*;
import com.xcd.clouddisk.entity.User;
import com.xcd.clouddisk.repository.UserRepository;
import com.xcd.clouddisk.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);
        
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(toUserDTO(user))
                .build();
        
        return ApiResponse.success(response);
    }
    
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error(400, "用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStorageLimit(5L * 1024 * 1024 * 1024L); // 5GB
        user.setStorageUsed(0L);
        
        user = userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(toUserDTO(user))
                .build();
        
        return ApiResponse.success(response);
    }
    
    public ApiResponse<UserDTO> getCurrentUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        return ApiResponse.success(toUserDTO(user));
    }
    
    public ApiResponse<StorageInfo> getStorageInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        
        StorageInfo info = new StorageInfo();
        info.setUsed(user.getStorageUsed());
        info.setLimit(user.getStorageLimit());
        info.setUsedPercentage(user.getStorageLimit() > 0 ? 
                (int) (user.getStorageUsed() * 100 / user.getStorageLimit()) : 0);
        
        return ApiResponse.success(info);
    }
    
    private UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .storageUsed(user.getStorageUsed())
                .storageLimit(user.getStorageLimit())
                .build();
    }
    
    @lombok.Data
    public static class StorageInfo {
        private Long used;
        private Long limit;
        private Integer usedPercentage;
        private Long fileCount;
    }
}
