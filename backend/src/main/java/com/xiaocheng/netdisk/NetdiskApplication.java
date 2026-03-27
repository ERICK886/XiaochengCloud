package com.xiaocheng.netdisk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xiaocheng.netdisk.mapper")
public class NetdiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetdiskApplication.class, args);
    }
}
