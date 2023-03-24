package com.friday.tool.base;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.function.Supplier;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/18 17:33
 * @Description BaseJob
 */
@Slf4j
public abstract class BaseJob {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 定义键
     *
     * @return 键
     */
    public abstract String lockerKey();

    /**
     * @param job
     */
    public void executeWithLocker(Supplier<Boolean> job) {
        RLock locker = redissonClient.getLock(lockerKey());
        boolean locked = locker.tryLock();
        if (locked) {
            Boolean result = job.get();
            log.info("Get job execute result: {}", result);

            locker.unlock();
        }
    }

}
