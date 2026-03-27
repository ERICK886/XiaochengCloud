package com.xiaocheng.netdisk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("file")
public class File {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long parentId;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private String mimeType;

    private Integer isFolder;

    private String fileHash;

    private String storagePath;

    private String thumbnail;

    private Integer isFavorite;

    private Integer isDeleted;

    private LocalDateTime deleteTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
