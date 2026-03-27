package com.xiaocheng.netdisk.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

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

    private Date expireTime;

    private Integer viewCount;

    private Integer downloadCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
