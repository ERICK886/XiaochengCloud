package com.xiaocheng.netdisk.common.enums;

public enum QualityMode {
    ORIGINAL(1, "原图"),
    COMPRESSED(2, "压缩");

    private final int code;
    private final String desc;

    QualityMode(int code, String desc) {
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
