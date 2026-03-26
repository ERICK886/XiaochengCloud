package com.xcd.clouddisk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private Long storageUsed;
    private Long storageLimit;
}
