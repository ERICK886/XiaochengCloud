package com.xcd.clouddisk.dto;

import lombok.Data;
import java.util.List;

@Data
public class FileListResponse {
    private List<FileDTO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
}
