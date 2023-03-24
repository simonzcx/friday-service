package com.friday.tool.task;

import com.friday.tool.base.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/18 17:52
 * @Description 数据同步任务
 */
@Slf4j
@Component
public class DataSyncTask extends BaseJob {

    @Override
    public String lockerKey() {
        return "onlineradio:task";
//        return "onlinemusic-task";
    }

    //@Scheduled(cron = "*/5 * * * * ?")
    public void execute() throws InterruptedException {
        this.executeWithLocker(() -> {
            boolean result = false;
            try {
                System.out.println("----test timed task----");
                executeTask();
                result = true;
            } catch (Exception e){
                log.error("task execute error: {}", e.getMessage());
            }
            return result;
        });
    }

    /**
     * 执行任务
     */
    private void executeTask() {
    }
}
