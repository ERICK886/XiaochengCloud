package com.xiaocheng.netdisk.api.dto;

import lombok.Data;

@Data
public class FileQueryDTO {

    private Long parentId;
    private String keyword;
    private String fileType;
    private String sortField;
    private String sortOrder;
    private Integer page = 1;
    private Integer pageSize = 20;
}
