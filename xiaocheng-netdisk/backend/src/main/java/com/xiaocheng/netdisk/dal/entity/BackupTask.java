package com.xiaocheng.netdisk.dal.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("backup_task")
public class BackupTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer taskType;

    private String sourcePath;

    private String targetPath;

    private Integer backupMode;

    private Integer qualityMode;

    private Integer status;

    private Date lastBackupTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
