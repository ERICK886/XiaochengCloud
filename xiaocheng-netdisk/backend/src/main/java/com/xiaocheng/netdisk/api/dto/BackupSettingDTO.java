package com.xiaocheng.netdisk.api.dto;

import lombok.Data;

@Data
public class BackupSettingDTO {

    private Integer taskType;

    private String sourcePath;

    private String targetPath;

    private Integer backupMode;

    private Integer qualityMode;
}
