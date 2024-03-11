package com.cclu.timer.service.scheduler;

import com.cclu.timer.common.conf.SchedulerAppConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author ChangCheng Lu
 * @description 调度器
 */
@Component
@Slf4j
public class SchedulerWorker {

    @Resource
    SchedulerTask schedulerTask;

    @Resource
    SchedulerAppConf schedulerAppConf;

    /**
     * 每1秒(1000毫秒)主动轮询，保障定时任务执行的秒级误差
     */
    @Scheduled(fixedRate = 1000)
    public void scheduledTask() {
        log.info("任务执行时间: " + LocalDateTime.now());
        handleSlices();
    }
    
    private void handleSlices(){
        for (int i = 0; i < schedulerAppConf.getBucketsNum(); i++) {
            handleSlice(i);
        }
    }

    /**
     *
     * @param bucketId bucketId
     */
    private void handleSlice(int bucketId){
        Date now = new Date();
        // 此处会重复执行一下前一分钟的批次。
        // 主要是为了一个兜底重试机制，如果某一个分片执行失败了，那这种方式可以在下一分钟进行一次重试。
        /*
           如果分片执行成功了，会延长分布式锁的时间，这样"重复执行前一分钟"批次这个逻辑也因为获取不到分布式
           锁而避免重复执行。如果分片本身执行失败了，则不会延长分布式锁时间，"重复执行前一分钟"则可以获取到分布式锁
           重试执行。
         */
        Date nowPreMin = new Date(now.getTime() - 60000);
        try {
            schedulerTask.asyncHandleSlice(nowPreMin, bucketId);
        }catch (Exception e){
            log.error("[handle slice] submit nowPreMin task failed, err:",e);
        }

        try {
            schedulerTask.asyncHandleSlice(now, bucketId);
        }catch (Exception e){
            log.error("[handle slice] submit now task failed, err: ", e);
        }
    }
}
