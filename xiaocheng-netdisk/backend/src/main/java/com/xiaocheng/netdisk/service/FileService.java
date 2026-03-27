package com.xiaocheng.netdisk.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaocheng.netdisk.api.dto.FileQueryDTO;
import com.xiaocheng.netdisk.api.dto.FileOperationDTO;
import com.xiaocheng.netdisk.common.enums.ResponseCode;
import com.xiaocheng.netdisk.common.exception.BusinessException;
import com.xiaocheng.netdisk.dal.entity.File;
import com.xiaocheng.netdisk.dal.entity.Recycle;
import com.xiaocheng.netdisk.dal.entity.User;
import com.xiaocheng.netdisk.dal.mapper.FileMapper;
import com.xiaocheng.netdisk.dal.mapper.RecycleMapper;
import com.xiaocheng.netdisk.dal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;
    private final RecycleMapper recycleMapper;
    private final UserMapper userMapper;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.temp-path}")
    private String tempPath;

    @Value("${file.chunk-path}")
    private String chunkPath;

    public IPage<File> getFileList(Long userId, FileQueryDTO queryDTO) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_deleted", 0);
        wrapper.eq(queryDTO.getParentId() != null, "parent_id", queryDTO.getParentId());

        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.like("file_name", queryDTO.getKeyword());
        }

        if (queryDTO.getFileType() != null && !queryDTO.getFileType().isEmpty()) {
            wrapper.like("file_type", queryDTO.getFileType());
        }

        String sortField = queryDTO.getSortField() != null ? queryDTO.getSortField() : "create_time";
        String sortOrder = queryDTO.getSortOrder() != null ? queryDTO.getSortOrder() : "desc";
        wrapper.orderBy(true, "desc".equals(sortOrder), sortField);

        Page<File> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        return fileMapper.selectPage(page, wrapper);
    }

    public List<File> getRecentFiles(Long userId, int limit) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_deleted", 0);
        wrapper.orderBy(true, false, "update_time");
        wrapper.last("LIMIT " + limit);
        return fileMapper.selectList(wrapper);
    }

    public List<File> getFavoriteFiles(Long userId) {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_deleted", 0);
        wrapper.eq("is_favorite", 1);
        wrapper.orderBy(true, false, "update_time");
        return fileMapper.selectList(wrapper);
    }

    @Transactional
    public File uploadFile(Long userId, MultipartFile file, Long parentId) throws IOException {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "用户不存在");
        }

        long fileSize = file.getSize();
        if (user.getTotalCapacity() - user.getUsedCapacity() < fileSize) {
            throw new BusinessException(ResponseCode.CAPACITY_EXCEEDED.getCode(), "容量不足");
        }

        String fileHash = calculateFileHash(file.getBytes());
        QueryWrapper<File> hashWrapper = new QueryWrapper<>();
        hashWrapper.eq("user_id", userId);
        hashWrapper.eq("file_hash", fileHash);
        hashWrapper.eq("is_deleted", 0);
        File existingFile = fileMapper.selectOne(hashWrapper);

        if (existingFile != null) {
            user.setUsedCapacity(user.getUsedCapacity() + fileSize);
            userMapper.updateById(user);
            return existingFile;
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = getFileType(originalFilename);
        String storagePath = saveFile(file, userId, fileHash);

        File newFile = new File();
        newFile.setUserId(userId);
        newFile.setParentId(parentId);
        newFile.setFileName(originalFilename);
        newFile.setFilePath("/" + originalFilename);
        newFile.setFileSize(fileSize);
        newFile.setFileType(fileType);
        newFile.setMimeType(file.getContentType());
        newFile.setIsFolder(0);
        newFile.setFileHash(fileHash);
        newFile.setStoragePath(storagePath);
        newFile.setIsFavorite(0);
        newFile.setIsDeleted(0);
        fileMapper.insert(newFile);

        user.setUsedCapacity(user.getUsedCapacity() + fileSize);
        userMapper.updateById(user);

        return newFile;
    }

    @Transactional
    public File createFolder(Long userId, String folderName, Long parentId) {
        File folder = new File();
        folder.setUserId(userId);
        folder.setParentId(parentId);
        folder.setFileName(folderName);
        folder.setFilePath("/" + folderName);
        folder.setIsFolder(1);
        folder.setIsFavorite(0);
        folder.setIsDeleted(0);
        fileMapper.insert(folder);
        return folder;
    }

    @Transactional
    public void deleteFile(Long userId, Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "文件不存在");
        }

        Recycle recycle = new Recycle();
        recycle.setUserId(userId);
        recycle.setFileId(file.getId());
        recycle.setFileName(file.getFileName());
        recycle.setFilePath(file.getFilePath());
        recycle.setFileSize(file.getFileSize());
        recycle.setFileType(file.getFileType());
        recycle.setDeleteTime(new Date());
        recycle.setExpireTime(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000));
        recycleMapper.insert(recycle);

        file.setIsDeleted(1);
        file.setDeleteTime(new Date());
        fileMapper.updateById(file);

        User user = userMapper.selectById(userId);
        user.setUsedCapacity(user.getUsedCapacity() - file.getFileSize());
        userMapper.updateById(user);
    }

    @Transactional
    public void moveFile(Long userId, Long fileId, Long targetParentId) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "文件不存在");
        }
        file.setParentId(targetParentId);
        fileMapper.updateById(file);
    }

    @Transactional
    public void renameFile(Long userId, Long fileId, String newName) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "文件不存在");
        }
        file.setFileName(newName);
        fileMapper.updateById(file);
    }

    @Transactional
    public void favoriteFile(Long userId, Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "文件不存在");
        }
        file.setIsFavorite(1);
        fileMapper.updateById(file);
    }

    @Transactional
    public void unfavoriteFile(Long userId, Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "文件不存在");
        }
        file.setIsFavorite(0);
        fileMapper.updateById(file);
    }

    public File getFileById(Long fileId) {
        return fileMapper.selectById(fileId);
    }

    private String saveFile(MultipartFile file, Long userId, String fileHash) throws IOException {
        Path userDir = Paths.get(uploadPath, String.valueOf(userId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }
        String extension = "";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = fileHash + extension;
        Path filePath = userDir.resolve(fileName);
        file.transferTo(filePath.toFile());
        return filePath.toString();
    }

    private String calculateFileHash(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(ResponseCode.FAIL.getCode(), "文件hash计算失败");
        }
    }

    private String getFileType(String fileName) {
        if (fileName == null) return "unknown";
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1) return "unknown";
        return fileName.substring(lastDot + 1).toLowerCase();
    }
}
