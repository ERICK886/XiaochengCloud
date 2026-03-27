package com.xiaocheng.netdisk.common.enums;

public enum TaskType {
    PHOTO(1, "相册备份"),
    VIDEO(2, "视频备份"),
    FOLDER(3, "文件夹同步");

    private final int code;
    private final String desc;

    TaskType(int code, String desc) {
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
