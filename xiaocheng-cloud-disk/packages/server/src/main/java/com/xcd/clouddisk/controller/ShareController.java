package com.xcd.clouddisk.controller;

import com.xcd.clouddisk.dto.ApiResponse;
import com.xcd.clouddisk.service.ShareService;
import com.xcd.clouddisk.service.ShareService.ShareDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {
    
    private final ShareService shareService;
    
    @PostMapping
    public ApiResponse<ShareDTO> createShare(
            Authentication authentication,
            @RequestBody ShareService.ShareRequest request) {
        return shareService.createShare((String) authentication.getPrincipal(), request);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ShareDTO> getShare(@PathVariable String id) {
        return shareService.getShare(id);
    }
    
    @PostMapping("/url")
    public ApiResponse<ShareDTO> getShareByUrl(@RequestBody Map<String, String> request) {
        return shareService.getShareByUrl(request.get("url"), request.get("password"));
    }
    
    @GetMapping
    public ApiResponse<List<ShareDTO>> getUserShares(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return shareService.getUserShares((String) authentication.getPrincipal(), page, pageSize);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<ShareDTO> updateShare(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody ShareService.ShareRequest request) {
        return ApiResponse.error("功能开发中");
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShare(
            Authentication authentication,
            @PathVariable String id) {
        return shareService.deleteShare((String) authentication.getPrincipal(), id);
    }
    
    @PostMapping("/{id}/save")
    public ApiResponse<Void> saveToMyDisk(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        return shareService.saveToMyDisk((String) authentication.getPrincipal(), id, request.get("targetFolderId"));
    }
    
    @GetMapping("/{id}/records")
    public ApiResponse<Map<String, List<?>>> getShareRecords(@PathVariable String id) {
        return ApiResponse.success(Map.of("views", List.of(), "downloads", List.of()));
    }
}
