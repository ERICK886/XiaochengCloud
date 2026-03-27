package com.xiaocheng.netdisk.api.controller;

import com.xiaocheng.netdisk.api.dto.CreateShareDTO;
import com.xiaocheng.netdisk.common.utils.ApiResponse;
import com.xiaocheng.netdisk.dal.entity.File;
import com.xiaocheng.netdisk.dal.entity.Share;
import com.xiaocheng.netdisk.dal.entity.ShareAccess;
import com.xiaocheng.netdisk.service.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @PostMapping("/create")
    public ApiResponse<Share> createShare(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateShareDTO dto) {
        Long userId = extractUserId(token);
        return ApiResponse.success(shareService.createShare(userId, dto));
    }

    @GetMapping("/list")
    public ApiResponse<List<Share>> getShareList(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ApiResponse.success(shareService.getShareList(userId));
    }

    @GetMapping("/detail")
    public ApiResponse<Share> getShareDetail(@RequestParam String shareCode) {
        return ApiResponse.success(shareService.getShareByCode(shareCode));
    }

    @GetMapping("/files")
    public ApiResponse<List<File>> getShareFiles(@RequestParam String shareCode) {
        return ApiResponse.success(shareService.getShareFiles(shareCode));
    }

    @PostMapping("/validate")
    public ApiResponse<Share> validateSharePassword(
            @RequestParam String shareCode,
            @RequestParam(required = false) String password) {
        return ApiResponse.success(shareService.validateSharePassword(shareCode, password));
    }

    @PostMapping("/access")
    public ApiResponse<Void> recordAccess(
            @RequestParam Long shareId,
            @RequestParam(required = false) Long accessUserId,
            @RequestParam Integer accessType,
            HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String deviceInfo = request.getHeader("User-Agent");
        shareService.recordShareAccess(shareId, accessUserId, accessType, ipAddress, deviceInfo);
        return ApiResponse.success();
    }

    @DeleteMapping("/cancel")
    public ApiResponse<Void> cancelShare(
            @RequestHeader("Authorization") String token,
            @RequestParam Long shareId) {
        Long userId = extractUserId(token);
        shareService.cancelShare(userId, shareId);
        return ApiResponse.success();
    }

    @GetMapping("/access/list")
    public ApiResponse<List<ShareAccess>> getShareAccessList(@RequestParam Long shareId) {
        return ApiResponse.success(shareService.getShareAccessList(shareId));
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

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
