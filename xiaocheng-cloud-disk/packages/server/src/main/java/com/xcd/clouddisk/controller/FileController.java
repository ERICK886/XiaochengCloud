package com.xcd.clouddisk.controller;

import com.xcd.clouddisk.dto.ApiResponse;
import com.xcd.clouddisk.dto.FileDTO;
import com.xcd.clouddisk.dto.FileListResponse;
import com.xcd.clouddisk.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;
    
    @GetMapping
    public ApiResponse<FileListResponse> getFiles(
            Authentication authentication,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return fileService.getFiles(
                (String) authentication.getPrincipal(),
                parentId, search, sortBy, sortOrder, page, pageSize);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<FileDTO> getFile(Authentication authentication, @PathVariable String id) {
        return fileService.getFile((String) authentication.getPrincipal(), id);
    }
    
    @PostMapping("/folder")
    public ApiResponse<FileDTO> createFolder(
            Authentication authentication,
            @RequestBody Map<String, String> request) {
        return fileService.createFolder(
                (String) authentication.getPrincipal(),
                request.get("name"),
                request.get("parentId"));
    }
    
    @PutMapping("/{id}/rename")
    public ApiResponse<FileDTO> rename(
            Authentication authentication,
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        return fileService.rename((String) authentication.getPrincipal(), id, request.get("filename"));
    }
    
    @PostMapping("/move")
    public ApiResponse<Void> moveFiles(
            Authentication authentication,
            @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> fileIds = (List<String>) request.get("fileIds");
        String targetParentId = (String) request.get("targetParentId");
        return fileService.moveFiles((String) authentication.getPrincipal(), fileIds, targetParentId);
    }
    
    @PostMapping("/copy")
    public ApiResponse<Void> copyFiles(
            Authentication authentication,
            @RequestBody Map<String, Object> request) {
        return ApiResponse.success(null);
    }
    
    @DeleteMapping
    public ApiResponse<Void> deleteFiles(
            Authentication authentication,
            @RequestBody Map<String, List<String>> request) {
        return fileService.deleteFiles((String) authentication.getPrincipal(), request.get("ids"));
    }
    
    @PostMapping("/restore")
    public ApiResponse<Void> restoreFiles(
            Authentication authentication,
            @RequestBody Map<String, List<String>> request) {
        return fileService.restoreFiles((String) authentication.getPrincipal(), request.get("ids"));
    }
    
    @DeleteMapping("/permanent")
    public ApiResponse<Void> permanentDeleteFiles(
            Authentication authentication,
            @RequestBody Map<String, List<String>> request) {
        return fileService.deleteFiles((String) authentication.getPrincipal(), request.get("ids"));
    }
    
    @PostMapping("/star")
    public ApiResponse<Void> starFiles(
            Authentication authentication,
            @RequestBody Map<String, List<String>> request) {
        return fileService.starFiles((String) authentication.getPrincipal(), request.get("ids"), true);
    }
    
    @PostMapping("/unstar")
    public ApiResponse<Void> unstarFiles(
            Authentication authentication,
            @RequestBody Map<String, List<String>> request) {
        return fileService.starFiles((String) authentication.getPrincipal(), request.get("ids"), false);
    }
    
    @GetMapping("/search")
    public ApiResponse<FileListResponse> searchFiles(
            Authentication authentication,
            @RequestParam String keyword,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        return fileService.getFiles((String) authentication.getPrincipal(), parentId, keyword, 
                null, "desc", page, pageSize);
    }
    
    @GetMapping("/recent")
    public ApiResponse<List<FileDTO>> getRecentFiles(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        return fileService.getRecentFiles((String) authentication.getPrincipal(), limit);
    }
    
    @GetMapping("/starred")
    public ApiResponse<List<FileDTO>> getStarredFiles(Authentication authentication) {
        return fileService.getStarredFiles((String) authentication.getPrincipal());
    }
    
    @GetMapping("/trash")
    public ApiResponse<List<FileDTO>> getTrashFiles(Authentication authentication) {
        return fileService.getTrashFiles((String) authentication.getPrincipal());
    }
    
    @GetMapping("/{id}/download")
    public ApiResponse<Map<String, String>> getDownloadUrl(
            Authentication authentication,
            @PathVariable String id) {
        return ApiResponse.success(Map.of("url", "/api/files/" + id + "/download"));
    }
}
