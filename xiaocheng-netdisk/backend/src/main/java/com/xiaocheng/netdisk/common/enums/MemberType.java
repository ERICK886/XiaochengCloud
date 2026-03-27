package com.xiaocheng.netdisk.common.enums;

public enum MemberType {
    NORMAL(0, "普通用户"),
    VIP(1, "会员");

    private final int code;
    private final String desc;

    MemberType(int code, String desc) {
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
