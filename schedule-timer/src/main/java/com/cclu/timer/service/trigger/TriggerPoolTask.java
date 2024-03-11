package com.cclu.timer.service.trigger;

import com.cclu.timer.model.TaskModel;
import com.cclu.timer.service.executor.ExecutorWorker;
import com.cclu.timer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ChangCheng Lu
 * @description 触发器池子
 */
@Slf4j
@Component
public class TriggerPoolTask {

    @Resource
    ExecutorWorker executorWorker;

    @Async("triggerPool")
    public void runExecutor(TaskModel task) {
        if(task == null){
            return;
        }
        log.info("start runExecutor");

        executorWorker.work(TimerUtils.unionTimerIdUnix(task.getTimerId(), task.getRunTimer()));

        log.info("end executeAsync");
    }
}
