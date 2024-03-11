package com.cclu.timer.service.trigger;

import com.cclu.timer.common.conf.TriggerAppConf;
import com.cclu.timer.mapper.TaskMapper;
import com.cclu.timer.redis.TaskCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

/**
 * @author ChangCheng Lu
 * @description 触发器
 */
@Component
@Slf4j
public class TriggerWorker {

    @Resource
    TriggerAppConf triggerAppConf;

    @Resource
    TriggerPoolTask triggerPoolTask;

    @Resource
    TaskCache taskCache;

    @Resource
    TaskMapper taskMapper;

    public void work(String minuteBucketKey){
        // 获取 1 分钟范围zrange切片
        Date startTime = getStartMinute(minuteBucketKey);
        Date endTime = new Date(startTime.getTime() + 60000);

        CountDownLatch latch = new CountDownLatch(1);
        Timer timer = new Timer("Timer");
        TriggerTimerTask task = new TriggerTimerTask(triggerAppConf, triggerPoolTask, taskCache, taskMapper, latch, startTime, endTime, minuteBucketKey);
        timer.scheduleAtFixedRate(task, 0L, triggerAppConf.getZrangeGapSeconds() * 1000L);
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("执行TriggerTimerTask异常中断，task:"+task);
        }finally{
            timer.cancel();
        }
    }

    private Date getStartMinute(String minuteBucketKey){
        String[] timeBucket = minuteBucketKey.split("_");
        if(timeBucket.length != 2){
            log.error("TriggerWorker getStartMinute 错误");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startMinute = null;
        try {
            startMinute = sdf.parse(timeBucket[0]);
        } catch (ParseException e) {
            log.error("TriggerWorker getStartMinute 错误");
        }
        return startMinute;
    }
}
