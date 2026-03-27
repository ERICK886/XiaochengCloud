package com.xiaocheng.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiaocheng.netdisk.entity.File;
import com.xiaocheng.netdisk.mapper.FileMapper;
import com.xiaocheng.netdisk.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public List<File> getFileList(Long userId, Long parentId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getUserId, userId)
                .eq(parentId == null, File::getParentId, null)
                .eq(parentId != null, File::getParentId, parentId)
                .eq(File::getIsDeleted, 0)
                .orderByDesc(File::getIsFolder)
                .orderByDesc(File::getUpdateTime);
        return fileMapper.selectList(wrapper);
    }

    @Override
    public File createFolder(Long userId, Long parentId, String folderName) {
        File folder = new File();
        folder.setUserId(userId);
        folder.setParentId(parentId);
        folder.setFileName(folderName);
        folder.setIsFolder(1);
        folder.setIsDeleted(0);
        folder.setIsFavorite(0);
        folder.setCreateTime(LocalDateTime.now());
        folder.setUpdateTime(LocalDateTime.now());
        fileMapper.insert(folder);
        return folder;
    }

    @Override
    public File getFileById(Long fileId) {
        return fileMapper.selectById(fileId);
    }

    @Override
    public void deleteFile(Long userId, Long fileId) {
        File file = fileMapper.selectOne(
            new LambdaQueryWrapper<File>()
                .eq(File::getId, fileId)
                .eq(File::getUserId, userId)
        );
        if (file != null) {
            file.setIsDeleted(1);
            file.setDeleteTime(LocalDateTime.now());
            fileMapper.updateById(file);
        }
    }

    @Override
    public void moveFile(Long userId, Long fileId, Long targetParentId) {
        File file = fileMapper.selectOne(
            new LambdaQueryWrapper<File>()
                .eq(File::getId, fileId)
                .eq(File::getUserId, userId)
        );
        if (file != null) {
            file.setParentId(targetParentId);
            file.setUpdateTime(LocalDateTime.now());
            fileMapper.updateById(file);
        }
    }

    @Override
    public void renameFile(Long userId, Long fileId, String newName) {
        File file = fileMapper.selectOne(
            new LambdaQueryWrapper<File>()
                .eq(File::getId, fileId)
                .eq(File::getUserId, userId)
        );
        if (file != null) {
            file.setFileName(newName);
            file.setUpdateTime(LocalDateTime.now());
            fileMapper.updateById(file);
        }
    }

    @Override
    public List<File> searchFiles(Long userId, String keyword) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getUserId, userId)
                .eq(File::getIsDeleted, 0)
                .like(File::getFileName, keyword)
                .orderByDesc(File::getUpdateTime);
        return fileMapper.selectList(wrapper);
    }

    @Override
    public void favoriteFile(Long userId, Long fileId) {
        LambdaUpdateWrapper<File> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(File::getId, fileId).eq(File::getUserId, userId)
                .set(File::getIsFavorite, 1);
        fileMapper.update(null, wrapper);
    }

    @Override
    public void unfavoriteFile(Long userId, Long fileId) {
        LambdaUpdateWrapper<File> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(File::getId, fileId).eq(File::getUserId, userId)
                .set(File::getIsFavorite, 0);
        fileMapper.update(null, wrapper);
    }

    @Override
    public List<File> getRecentFiles(Long userId, int limit) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getUserId, userId)
                .eq(File::getIsDeleted, 0)
                .orderByDesc(File::getUpdateTime)
                .last("LIMIT " + limit);
        return fileMapper.selectList(wrapper);
    }
}
