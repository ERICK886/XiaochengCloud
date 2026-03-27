package com.xiaocheng.netdisk.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaocheng.netdisk.api.dto.LoginDTO;
import com.xiaocheng.netdisk.api.dto.RegisterDTO;
import com.xiaocheng.netdisk.common.enums.ResponseCode;
import com.xiaocheng.netdisk.common.exception.BusinessException;
import com.xiaocheng.netdisk.common.utils.JwtUtil;
import com.xiaocheng.netdisk.dal.entity.User;
import com.xiaocheng.netdisk.dal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${file.default-capacity}")
    private Long defaultCapacity;

    public Map<String, Object> register(RegisterDTO dto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResponseCode.FAIL.getCode(), "用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setTotalCapacity(defaultCapacity);
        user.setUsedCapacity(0L);
        user.setMemberType(0);
        user.setStatus(0);
        userMapper.insert(user);

        String token = generateToken(user);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        return result;
    }

    public Map<String, Object> login(LoginDTO dto) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }

        if (user.getStatus() != 0) {
            throw new BusinessException(ResponseCode.FORBIDDEN.getCode(), "账号已被禁用");
        }

        String token = generateToken(user);
        redisTemplate.opsForValue().set("token:" + user.getId(), token, expiration, TimeUnit.MILLISECONDS);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("totalCapacity", user.getTotalCapacity());
        result.put("usedCapacity", user.getUsedCapacity());
        result.put("memberType", user.getMemberType());
        return result;
    }

    public void logout(Long userId) {
        redisTemplate.delete("token:" + userId);
    }

    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResponseCode.FAIL.getCode(), "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private String generateToken(User user) {
        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
