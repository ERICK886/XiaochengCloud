package com.xiaocheng.netdisk.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("share_access")
public class ShareAccess {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shareId;

    private Long accessUserId;

    private Integer accessType;

    private Date accessTime;

    private String ipAddress;

    private String deviceInfo;
}
