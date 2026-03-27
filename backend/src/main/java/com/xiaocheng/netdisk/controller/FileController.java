package com.xiaocheng.netdisk.controller;

import com.xiaocheng.netdisk.dto.ApiResponse;
import com.xiaocheng.netdisk.entity.File;
import com.xiaocheng.netdisk.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/list")
    public ApiResponse<List<File>> getFileList(Authentication authentication,
                                                @RequestParam(required = false) Long parentId) {
        Long userId = Long.parseLong(authentication.getName());
        List<File> files = fileService.getFileList(userId, parentId);
        return ApiResponse.success(files);
    }

    @PostMapping("/createFolder")
    public ApiResponse<File> createFolder(Authentication authentication,
                                           @RequestParam(required = false) Long parentId,
                                           @RequestParam String folderName) {
        Long userId = Long.parseLong(authentication.getName());
        File folder = fileService.createFolder(userId, parentId, folderName);
        return ApiResponse.success(folder);
    }

    @GetMapping("/detail")
    public ApiResponse<File> getFileDetail(@RequestParam Long fileId) {
        File file = fileService.getFileById(fileId);
        return ApiResponse.success(file);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteFile(Authentication authentication,
                                         @RequestParam Long fileId) {
        Long userId = Long.parseLong(authentication.getName());
        fileService.deleteFile(userId, fileId);
        return ApiResponse.success();
    }

    @PutMapping("/move")
    public ApiResponse<Void> moveFile(Authentication authentication,
                                       @RequestParam Long fileId,
                                       @RequestParam Long targetParentId) {
        Long userId = Long.parseLong(authentication.getName());
        fileService.moveFile(userId, fileId, targetParentId);
        return ApiResponse.success();
    }

    @PutMapping("/rename")
    public ApiResponse<Void> renameFile(Authentication authentication,
                                         @RequestParam Long fileId,
                                         @RequestParam String newName) {
        Long userId = Long.parseLong(authentication.getName());
        fileService.renameFile(userId, fileId, newName);
        return ApiResponse.success();
    }

    @GetMapping("/search")
    public ApiResponse<List<File>> searchFiles(Authentication authentication,
                                                @RequestParam String keyword) {
        Long userId = Long.parseLong(authentication.getName());
        List<File> files = fileService.searchFiles(userId, keyword);
        return ApiResponse.success(files);
    }

    @PostMapping("/favorite")
    public ApiResponse<Void> favoriteFile(Authentication authentication,
                                            @RequestParam Long fileId) {
        Long userId = Long.parseLong(authentication.getName());
        fileService.favoriteFile(userId, fileId);
        return ApiResponse.success();
    }

    @PostMapping("/unfavorite")
    public ApiResponse<Void> unfavoriteFile(Authentication authentication,
                                             @RequestParam Long fileId) {
        Long userId = Long.parseLong(authentication.getName());
        fileService.unfavoriteFile(userId, fileId);
        return ApiResponse.success();
    }

    @GetMapping("/recent")
    public ApiResponse<List<File>> getRecentFiles(Authentication authentication,
                                                   @RequestParam(defaultValue = "20") int limit) {
        Long userId = Long.parseLong(authentication.getName());
        List<File> files = fileService.getRecentFiles(userId, limit);
        return ApiResponse.success(files);
    }
}
