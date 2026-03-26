package com.xcd.clouddisk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    private String id;
    private String userId;
    private String parentId;
    private String filename;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String mimeType;
    private String thumbnail;
    private Boolean isFolder;
    private Boolean isDeleted;
    private String deletedAt;
    private Boolean isStarred;
    private String createdAt;
    private String updatedAt;
}
