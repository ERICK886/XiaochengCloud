package com.xcd.clouddisk.service;

import com.xcd.clouddisk.dto.ApiResponse;
import com.xcd.clouddisk.dto.FileDTO;
import com.xcd.clouddisk.entity.FileEntity;
import com.xcd.clouddisk.entity.Share;
import com.xcd.clouddisk.repository.FileRepository;
import com.xcd.clouddisk.repository.ShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ShareService {
    
    private final ShareRepository shareRepository;
    private final FileRepository fileRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Transactional
    public ApiResponse<ShareDTO> createShare(String userId, ShareRequest request) {
        FileEntity file = fileRepository.findById(request.getFileIds().get(0)).orElse(null);
        if (file == null || !file.getUserId().equals(userId)) {
            return ApiResponse.error(404, "文件不存在");
        }
        
        Share share = new Share();
        share.setUserId(userId);
        share.setFileId(request.getFileIds().get(0));
        share.setShareType(request.getShareType());
        share.setShareUrl(UUID.randomUUID().toString().replace("-", ""));
        share.setSharePassword(request.getPassword());
        share.setPermission(request.getPermission() != null ? request.getPermission() : "view");
        
        if (request.getExpiresAt() != null) {
            share.setExpiresAt(LocalDateTime.parse(request.getExpiresAt(), FORMATTER));
        }
        
        share = shareRepository.save(share);
        
        return ApiResponse.success(toShareDTO(share, file));
    }
    
    public ApiResponse<ShareDTO> getShare(String shareId) {
        Share share = shareRepository.findById(shareId).orElse(null);
        if (share == null) {
            return ApiResponse.error(404, "分享不存在");
        }
        
        FileEntity file = fileRepository.findById(share.getFileId()).orElse(null);
        
        return ApiResponse.success(toShareDTO(share, file));
    }
    
    public ApiResponse<ShareDTO> getShareByUrl(String shareUrl, String password) {
        Share share = shareRepository.findByShareUrl(shareUrl).orElse(null);
        if (share == null) {
            return ApiResponse.error(404, "分享不存在");
        }
        
        if (share.getExpiresAt() != null && share.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ApiResponse.error(410, "分享已过期");
        }
        
        if ("password".equals(share.getShareType())) {
            if (password == null || !password.equals(share.getSharePassword())) {
                return ApiResponse.error(403, "请输入正确的提取码");
            }
        }
        
        share.setViewCount(share.getViewCount() + 1);
        shareRepository.save(share);
        
        FileEntity file = fileRepository.findById(share.getFileId()).orElse(null);
        
        ShareDTO dto = toShareDTO(share, file);
        dto.setRequirePassword(!"password".equals(share.getShareType()) && share.getSharePassword() != null);
        
        return ApiResponse.success(dto);
    }
    
    public ApiResponse<List<ShareDTO>> getUserShares(String userId, Integer page, Integer pageSize) {
        Page<Share> shares = shareRepository.findByUserId(userId, 
                PageRequest.of(page != null ? page - 1 : 0, pageSize != null ? pageSize : 20,
                        Sort.by(Sort.Direction.DESC, "createdAt")));
        
        List<ShareDTO> items = shares.getContent().stream()
                .map(s -> {
                    FileEntity file = fileRepository.findById(s.getFileId()).orElse(null);
                    return toShareDTO(s, file);
                })
                .collect(Collectors.toList());
        
        return ApiResponse.success(items);
    }
    
    @Transactional
    public ApiResponse<Void> deleteShare(String userId, String shareId) {
        Share share = shareRepository.findById(shareId).orElse(null);
        if (share == null || !share.getUserId().equals(userId)) {
            return ApiResponse.error(404, "分享不存在");
        }
        
        shareRepository.delete(share);
        return ApiResponse.success(null);
    }
    
    @Transactional
    public ApiResponse<Void> saveToMyDisk(String userId, String shareId, String targetFolderId) {
        Share share = shareRepository.findById(shareId).orElse(null);
        if (share == null) {
            return ApiResponse.error(404, "分享不存在");
        }
        
        FileEntity originalFile = fileRepository.findById(share.getFileId()).orElse(null);
        if (originalFile == null) {
            return ApiResponse.error(404, "文件不存在");
        }
        
        FileEntity newFile = new FileEntity();
        newFile.setUserId(userId);
        newFile.setParentId(targetFolderId);
        newFile.setFilename(originalFile.getFilename());
        newFile.setFilePath(originalFile.getFilePath());
        newFile.setFileSize(originalFile.getFileSize());
        newFile.setFileType(originalFile.getFileType());
        newFile.setMimeType(originalFile.getMimeType());
        newFile.setIsFolder(originalFile.getIsFolder());
        
        fileRepository.save(newFile);
        
        return ApiResponse.success(null);
    }
    
    private ShareDTO toShareDTO(Share share, FileEntity file) {
        return ShareDTO.builder()
                .id(share.getId())
                .fileId(share.getFileId())
                .fileName(file != null ? file.getFilename() : "")
                .shareType(share.getShareType())
                .shareUrl(share.getShareUrl())
                .sharePassword(share.getSharePassword())
                .permission(share.getPermission())
                .expiresAt(share.getExpiresAt() != null ? share.getExpiresAt().format(FORMATTER) : null)
                .createdBy(share.getUserId())
                .viewCount(share.getViewCount())
                .downloadCount(share.getDownloadCount())
                .createdAt(share.getCreatedAt().format(FORMATTER))
                .updatedAt(share.getUpdatedAt().format(FORMATTER))
                .file(file != null ? FileDTO.builder()
                        .id(file.getId())
                        .filename(file.getFilename())
                        .fileSize(file.getFileSize())
                        .fileType(file.getFileType())
                        .isFolder(file.getIsFolder())
                        .build() : null)
                .build();
    }
    
    @lombok.Data
    public static class ShareRequest {
        private List<String> fileIds;
        private String shareType;
        private String password;
        private String permission;
        private String expiresAt;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ShareDTO {
        private String id;
        private String fileId;
        private String fileName;
        private String shareType;
        private String shareUrl;
        private String sharePassword;
        private String permission;
        private String expiresAt;
        private String createdBy;
        private Integer viewCount;
        private Integer downloadCount;
        private String createdAt;
        private String updatedAt;
        private FileDTO file;
        private Boolean requirePassword;
    }
}
