package com.xiaocheng.netdisk.schedule;

import com.xiaocheng.netdisk.service.RecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecycleCleanTask {

    private final RecycleService recycleService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredRecycle() {
        recycleService.cleanExpiredRecycle();
    }
}
