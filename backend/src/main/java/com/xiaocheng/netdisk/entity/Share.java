package com.xiaocheng.netdisk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("share")
public class Share {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String shareCode;

    private Long userId;

    private String fileIds;

    private Integer shareType;

    private String password;

    private LocalDateTime expireTime;

    private Integer viewCount;

    private Integer downloadCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
