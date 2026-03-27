package com.xiaocheng.netdisk.common.enums;

public enum BackupMode {
    WIFI_ONLY(1, "仅WiFi"),
    ALL_NETWORK(2, "所有网络");

    private final int code;
    private final String desc;

    BackupMode(int code, String desc) {
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
