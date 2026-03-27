package com.xiaocheng.netdisk.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FileOperationDTO {

    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    private String newName;

    private Long targetParentId;
}
