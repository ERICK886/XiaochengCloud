package com.xiaocheng.netdisk.api.controller;

import com.xiaocheng.netdisk.common.utils.ApiResponse;
import com.xiaocheng.netdisk.dal.entity.Recycle;
import com.xiaocheng.netdisk.service.RecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recycle")
@RequiredArgsConstructor
public class RecycleController {

    private final RecycleService recycleService;

    @GetMapping("/list")
    public ApiResponse<List<Recycle>> getRecycleList(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ApiResponse.success(recycleService.getRecycleList(userId));
    }

    @PostMapping("/restore")
    public ApiResponse<Void> restoreFile(
            @RequestHeader("Authorization") String token,
            @RequestParam Long recycleId) {
        Long userId = extractUserId(token);
        recycleService.restoreFile(userId, recycleId);
        return ApiResponse.success();
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> permanentlyDelete(
            @RequestHeader("Authorization") String token,
            @RequestParam Long recycleId) {
        Long userId = extractUserId(token);
        recycleService.permanentlyDelete(userId, recycleId);
        return ApiResponse.success();
    }

    @DeleteMapping("/empty")
    public ApiResponse<Void> emptyRecycle(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        recycleService.emptyRecycle(userId);
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
