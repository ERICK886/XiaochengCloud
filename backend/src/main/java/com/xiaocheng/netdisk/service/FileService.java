package com.xiaocheng.netdisk.service;

import com.xiaocheng.netdisk.entity.File;
import java.util.List;

public interface FileService {
    List<File> getFileList(Long userId, Long parentId);
    File createFolder(Long userId, Long parentId, String folderName);
    File getFileById(Long fileId);
    void deleteFile(Long userId, Long fileId);
    void moveFile(Long userId, Long fileId, Long targetParentId);
    void renameFile(Long userId, Long fileId, String newName);
    List<File> searchFiles(Long userId, String keyword);
    void favoriteFile(Long userId, Long fileId);
    void unfavoriteFile(Long userId, Long fileId);
    List<File> getRecentFiles(Long userId, int limit);
}
