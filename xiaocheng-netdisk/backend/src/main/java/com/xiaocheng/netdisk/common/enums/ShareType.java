package com.xiaocheng.netdisk.common.enums;

public enum ShareType {
    PUBLIC(1, "公开链接"),
    PASSWORD(2, "密码链接"),
    FRIEND(3, "指定好友");

    private final int code;
    private final String desc;

    ShareType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
