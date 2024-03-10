package com.cclu.timer.service.trigger;

import com.cclu.common.redis.ReentrantDistributeLock;
import com.cclu.timer.model.TaskModel;
import com.cclu.timer.service.executor.ExecutorWorker;
import com.cclu.timer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TriggerPoolTask {

    @Autowired
    ReentrantDistributeLock reentrantDistributeLock;

    @Autowired
    ExecutorWorker executorWorker;

    @Async("triggerPool")
    public void runExecutor(TaskModel task) {
        if(task == null){
            return;
        }
        log.info("start runExecutor");

        executorWorker.work(TimerUtils.UnionTimerIDUnix(task.getTimerId(),task.getRunTimer()));

        log.info("end executeAsync");
    }
}
