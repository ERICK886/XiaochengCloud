package com.xiaocheng.netdisk.service;

import com.xiaocheng.netdisk.entity.User;

public interface UserService {
    User register(String username, String password, String phone);
    String login(String username, String password);
    User getUserInfo(Long userId);
    void updatePassword(Long userId, String oldPassword, String newPassword);
}
