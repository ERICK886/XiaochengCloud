package com.xiaocheng.netdisk.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xiaocheng.netdisk.api.dto.FileQueryDTO;
import com.xiaocheng.netdisk.api.dto.FileOperationDTO;
import com.xiaocheng.netdisk.common.utils.ApiResponse;
import com.xiaocheng.netdisk.dal.entity.File;
import com.xiaocheng.netdisk.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    public ApiResponse<IPage<File>> getFileList(
            @RequestHeader("Authorization") String token,
            @ModelAttribute FileQueryDTO queryDTO) {
        Long userId = extractUserId(token);
        return ApiResponse.success(fileService.getFileList(userId, queryDTO));
    }

    @GetMapping("/recent")
    public ApiResponse<List<File>> getRecentFiles(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = extractUserId(token);
        return ApiResponse.success(fileService.getRecentFiles(userId, limit));
    }

    @GetMapping("/favorite")
    public ApiResponse<List<File>> getFavoriteFiles(@RequestHeader("Authorization") String token) {
        Long userId = extractUserId(token);
        return ApiResponse.success(fileService.getFavoriteFiles(userId));
    }

    @PostMapping("/upload")
    public ApiResponse<File> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long parentId) throws IOException {
        Long userId = extractUserId(token);
        return ApiResponse.success(fileService.uploadFile(userId, file, parentId));
    }

    @PostMapping("/folder")
    public ApiResponse<File> createFolder(
            @RequestHeader("Authorization") String token,
            @RequestParam String folderName,
            @RequestParam(required = false) Long parentId) {
        Long userId = extractUserId(token);
        return ApiResponse.success(fileService.createFolder(userId, folderName, parentId));
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteFile(
            @RequestHeader("Authorization") String token,
            @RequestParam Long fileId) {
        Long userId = extractUserId(token);
        fileService.deleteFile(userId, fileId);
        return ApiResponse.success();
    }

    @PutMapping("/move")
    public ApiResponse<Void> moveFile(
            @RequestHeader("Authorization") String token,
            @RequestParam Long fileId,
            @RequestParam Long targetParentId) {
        Long userId = extractUserId(token);
        fileService.moveFile(userId, fileId, targetParentId);
        return ApiResponse.success();
    }

    @PutMapping("/rename")
    public ApiResponse<Void> renameFile(
            @RequestHeader("Authorization") String token,
            @RequestParam Long fileId,
            @RequestParam String newName) {
        Long userId = extractUserId(token);
        fileService.renameFile(userId, fileId, newName);
        return ApiResponse.success();
    }

    @PostMapping("/favorite")
    public ApiResponse<Void> favoriteFile(
            @RequestHeader("Authorization") String token,
            @RequestParam Long fileId) {
        Long userId = extractUserId(token);
        fileService.favoriteFile(userId, fileId);
        return ApiResponse.success();
    }

    @PostMapping("/unfavorite")
    public ApiResponse<Void> unfavoriteFile(
            @RequestHeader("Authorization") String token,
            @RequestParam Long fileId) {
        Long userId = extractUserId(token);
        fileService.unfavoriteFile(userId, fileId);
        return ApiResponse.success();
    }

    @GetMapping("/detail")
    public ApiResponse<File> getFileDetail(@RequestParam Long fileId) {
        return ApiResponse.success(fileService.getFileById(fileId));
    }

    @GetMapping("/download")
    public void downloadFile(
            @RequestParam Long fileId,
            HttpServletResponse response) throws IOException {
        File file = fileService.getFileById(fileId);
        if (file != null && file.getStoragePath() != null) {
            java.io.File fileObj = new java.io.File(file.getStoragePath());
            if (fileObj.exists()) {
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
                try (java.io.FileInputStream fis = new java.io.FileInputStream(fileObj);
                     org.apache.commons.io.IOUtils.copy(fis, response.getOutputStream())) {
                }
            }
        }
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
