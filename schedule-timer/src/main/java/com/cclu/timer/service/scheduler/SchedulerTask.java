package com.cclu.timer.service.scheduler;

import com.cclu.common.redis.ReentrantDistributeLock;
import com.cclu.timer.common.conf.SchedulerAppConf;
import com.cclu.timer.service.trigger.TriggerWorker;
import com.cclu.timer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author ChangCheng Lu
 * @description 调度任务
 */
@Slf4j
@Component
public class SchedulerTask {

    @Resource
    ReentrantDistributeLock reentrantDistributeLock;

    @Resource
    SchedulerAppConf schedulerAppConf;

    @Resource
    TriggerWorker triggerWorker;

    @Async("schedulerPool")
    public void asyncHandleSlice(Date date, int bucketId) {
        log.info("start executeAsync");

        String lockToken = TimerUtils.getTokenStr();
        // 只加锁不解锁，只有超时解锁；超时时间控制频率；
        // 锁住横纵向切分后的单个桶(分钟级别 + bucketIndex)
        // 按照时间横向切片、按照Bucket纵向切分
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.getTimeBucketLockKey(date, bucketId),
                lockToken,
                schedulerAppConf.getTryLockSeconds());
        if(!ok){
            log.info("asyncHandleSlice 获取分布式锁失败");
            return;
        }

        log.info("get scheduler lock success, key: " + TimerUtils.getTimeBucketLockKey(date, bucketId));

        // 调用trigger进行处理
        triggerWorker.work(TimerUtils.getSliceMsgKey(date, bucketId));

        // 延长分布式锁的时间, 避免重复执行分片
        reentrantDistributeLock.expireLock(
                TimerUtils.getTimeBucketLockKey(date, bucketId),
                lockToken,
                schedulerAppConf.getSuccessExpireSeconds());

        log.info("end executeAsync");
    }
}
