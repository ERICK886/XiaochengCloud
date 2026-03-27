package com.xiaocheng.netdisk.api.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CreateShareDTO {

    @NotEmpty(message = "文件ID列表不能为空")
    private List<Long> fileIds;

    private Integer shareType;

    private String password;

    private Integer expireDays;
}
