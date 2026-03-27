package com.xiaocheng.netdisk.api.controller;

import com.xiaocheng.netdisk.api.dto.BackupSettingDTO;
import com.xiaocheng.netdisk.common.utils.ApiResponse;
import com.xiaocheng.netdisk.dal.entity.BackupTask;
import com.xiaocheng.netdisk.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/task")
    public ApiResponse<BackupTask> createBackupTask(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody BackupSettingDTO dto) {
        Long userId = extractUserId(token);
        return ApiResponse.success(backupService.createBackupTask(userId, dto));
    }

    @GetMapping("/task/list")
    public ApiResponse<List<BackupTask>> getBackupTaskList(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ApiResponse.success(backupService.getBackupTaskList(userId));
    }

    @GetMapping("/task/detail")
    public ApiResponse<BackupTask> getBackupTaskDetail(@RequestParam Long taskId) {
        return ApiResponse.success(backupService.getBackupTaskById(taskId));
    }

    @PutMapping("/task")
    public ApiResponse<Void> updateBackupTask(
            @RequestHeader("Authorization") String token,
            @RequestParam Long taskId,
            @RequestBody BackupSettingDTO dto) {
        Long userId = extractUserId(token);
        backupService.updateBackupTask(userId, taskId, dto);
        return ApiResponse.success();
    }

    @PutMapping("/task/status")
    public ApiResponse<Void> updateBackupTaskStatus(
            @RequestParam Long taskId,
            @RequestParam Integer status) {
        backupService.updateBackupTaskStatus(taskId, status);
        return ApiResponse.success();
    }

    @DeleteMapping("/task")
    public ApiResponse<Void> deleteBackupTask(
            @RequestHeader("Authorization") String token,
            @RequestParam Long taskId) {
        Long userId = extractUserId(token);
        backupService.deleteBackupTask(userId, taskId);
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
