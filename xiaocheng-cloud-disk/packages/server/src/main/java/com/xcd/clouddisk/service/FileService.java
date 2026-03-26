package com.xcd.clouddisk.service;

import com.xcd.clouddisk.dto.ApiResponse;
import com.xcd.clouddisk.dto.FileDTO;
import com.xcd.clouddisk.dto.FileListResponse;
import com.xcd.clouddisk.entity.FileEntity;
import com.xcd.clouddisk.repository.FileRepository;
import com.xcd.clouddisk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ApiResponse<FileListResponse> getFiles(String userId, String parentId, String search,
                                                   String sortBy, String sortOrder, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(
                page != null ? page - 1 : 0,
                pageSize != null ? pageSize : 100,
                Sort.by(Sort.Direction.fromString(sortOrder != null ? sortOrder : "DESC"),
                        sortBy != null ? sortBy : "updatedAt")
        );
        
        Page<FileEntity> filePage;
        
        if (search != null && !search.isEmpty()) {
            filePage = fileRepository.searchFiles(userId, search, pageable);
        } else if (parentId != null) {
            filePage = fileRepository.findByUserIdAndParentIdAndIsDeleted(userId, parentId, false, pageable);
        } else {
            filePage = fileRepository.findByUserIdAndIsDeleted(userId, false, pageable);
        }
        
        List<FileDTO> items = filePage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        FileListResponse response = new FileListResponse();
        response.setItems(items);
        response.setTotal(filePage.getTotalElements());
        response.setPage(page != null ? page : 1);
        response.setPageSize(pageSize != null ? pageSize : 100);
        
        return ApiResponse.success(response);
    }
    
    public ApiResponse<FileDTO> getFile(String userId, String fileId) {
        FileEntity file = fileRepository.findById(fileId).orElse(null);
        if (file == null || !file.getUserId().equals(userId)) {
            return ApiResponse.error(404, "文件不存在");
        }
        return ApiResponse.success(toDTO(file));
    }
    
    @Transactional
    public ApiResponse<FileDTO> createFolder(String userId, String name, String parentId) {
        FileEntity folder = new FileEntity();
        folder.setUserId(userId);
        folder.setParentId(parentId);
        folder.setFilename(name);
        folder.setIsFolder(true);
        folder.setFileType("folder");
        
        folder = fileRepository.save(folder);
        
        return ApiResponse.success(toDTO(folder));
    }
    
    @Transactional
    public ApiResponse<FileDTO> rename(String userId, String fileId, String filename) {
        FileEntity file = fileRepository.findById(fileId).orElse(null);
        if (file == null || !file.getUserId().equals(userId)) {
            return ApiResponse.error(404, "文件不存在");
        }
        
        file.setFilename(filename);
        file = fileRepository.save(file);
        
        return ApiResponse.success(toDTO(file));
    }
    
    @Transactional
    public ApiResponse<Void> deleteFiles(String userId, List<String> fileIds) {
        List<FileEntity> files = fileRepository.findByIdIn(fileIds);
        
        for (FileEntity file : files) {
            if (file.getUserId().equals(userId)) {
                file.setIsDeleted(true);
                file.setDeletedAt(LocalDateTime.now());
                fileRepository.save(file);
            }
        }
        
        return ApiResponse.success(null);
    }
    
    @Transactional
    public ApiResponse<Void> restoreFiles(String userId, List<String> fileIds) {
        List<FileEntity> files = fileRepository.findByIdIn(fileIds);
        
        for (FileEntity file : files) {
            if (file.getUserId().equals(userId)) {
                file.setIsDeleted(false);
                file.setDeletedAt(null);
                fileRepository.save(file);
            }
        }
        
        return ApiResponse.success(null);
    }
    
    @Transactional
    public ApiResponse<Void> moveFiles(String userId, List<String> fileIds, String targetParentId) {
        List<FileEntity> files = fileRepository.findByIdIn(fileIds);
        
        for (FileEntity file : files) {
            if (file.getUserId().equals(userId)) {
                file.setParentId(targetParentId);
                fileRepository.save(file);
            }
        }
        
        return ApiResponse.success(null);
    }
    
    @Transactional
    public ApiResponse<Void> starFiles(String userId, List<String> fileIds, boolean star) {
        List<FileEntity> files = fileRepository.findByIdIn(fileIds);
        
        for (FileEntity file : files) {
            if (file.getUserId().equals(userId)) {
                file.setIsStarred(star);
                fileRepository.save(file);
            }
        }
        
        return ApiResponse.success(null);
    }
    
    public ApiResponse<List<FileDTO>> getStarredFiles(String userId) {
        List<FileEntity> files = fileRepository.findByUserIdAndIsStarredAndIsDeleted(userId, true, false);
        List<FileDTO> items = files.stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.success(items);
    }
    
    public ApiResponse<List<FileDTO>> getRecentFiles(String userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<FileEntity> files = fileRepository.findByUserIdAndIsDeleted(userId, false, pageable);
        List<FileDTO> items = files.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.success(items);
    }
    
    public ApiResponse<List<FileDTO>> getTrashFiles(String userId) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "deletedAt"));
        Page<FileEntity> files = fileRepository.findByUserIdAndIsDeleted(userId, true, pageable);
        List<FileDTO> items = files.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return ApiResponse.success(items);
    }
    
    private FileDTO toDTO(FileEntity file) {
        return FileDTO.builder()
                .id(file.getId())
                .userId(file.getUserId())
                .parentId(file.getParentId())
                .filename(file.getFilename())
                .filePath(file.getFilePath())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .mimeType(file.getMimeType())
                .thumbnail(file.getThumbnail())
                .isFolder(file.getIsFolder())
                .isDeleted(file.getIsDeleted())
                .deletedAt(file.getDeletedAt() != null ? file.getDeletedAt().format(FORMATTER) : null)
                .isStarred(file.getIsStarred())
                .createdAt(file.getCreatedAt().format(FORMATTER))
                .updatedAt(file.getUpdatedAt().format(FORMATTER))
                .build();
    }
}
