package com.xiaocheng.netdisk.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("recycle")
public class Recycle {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long fileId;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private Date deleteTime;

    private Date expireTime;
}
