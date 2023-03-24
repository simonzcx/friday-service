package com.friday.tool.task;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author e99g41y
 * @date 2022/8/15 10:47
 */
@Component
@Slf4j
public class AccountSchedule {
    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "*/5 * * * * ?")
    public void syncCollection() {
        log.info("trigger schema {},{}", "syncCollection", new Date());
        RLock rLock = redissonClient.getLock("onlineradio:task");
        if (rLock.tryLock()) {
            System.out.println("----test timed task----");
        }
        rLock.unlock();
    }
}