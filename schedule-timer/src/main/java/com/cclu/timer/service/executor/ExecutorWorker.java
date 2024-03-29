package com.cclu.timer.service.executor;

import com.cclu.api.dto.timer.NotifyHttpParam;
import com.cclu.api.dto.timer.TimerDTO;
import com.cclu.timer.enums.TaskStatus;
import com.cclu.timer.enums.TimerStatus;
import com.cclu.timer.exception.BusinessException;
import com.cclu.timer.exception.ErrorCode;
import com.cclu.timer.mapper.TaskMapper;
import com.cclu.timer.mapper.TimerMapper;
import com.cclu.timer.model.TaskModel;
import com.cclu.timer.model.TimerModel;
import com.cclu.timer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ChangCheng Lu
 * @description 执行器
 */
@Component
@Slf4j
public class ExecutorWorker {

    @Resource
    TaskMapper taskMapper;

    @Resource
    TimerMapper timerMapper;

    public void work(String timerIdUnixKey){
        List<Long> longSet = TimerUtils.splitTimerIdUnix(timerIdUnixKey);
        if(longSet.size() != 2){
            log.error("splitTimerIDUnix 错误, timerIDUnix:"+timerIdUnixKey);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"splitTimerIDUnix 错误, timerIDUnix:"+timerIdUnixKey);
        }
        Long timerId = longSet.get(0);
        Long unix = longSet.get(1);
        TaskModel task = taskMapper.getTasksByTimerIdUnix(timerId, unix);
        if(task.getStatus() != TaskStatus.NotRun.getStatus()){
            log.warn("重复执行任务timerId: " + timerId + ", runtime:"+unix);
            return;
        }

        // 执行回调
        executeAndPostProcess(task, timerId, unix);
    }

    private void executeAndPostProcess(TaskModel taskModel, Long timerId, Long unix){
        TimerModel timerModel = timerMapper.getTimerById(timerId);
        if(timerModel == null){
            log.error("执行回调错误，找不到对应的Timer。 timerId"+timerId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"执行回调错误，找不到对应的Timer。 timerId"+timerId);
        }

        if(timerModel.getStatus() != TimerStatus.Enable.getStatus()){
            log.warn("Timer已经处于去激活状态。 timerId"+timerId);
            return;
        }

        // 触发时间的误差误差时间
        int gapTime = (int) (System.currentTimeMillis() - taskModel.getRunTimer());
        taskModel.setCostTime(gapTime);

        // 执行http回调，通知业务放
        ResponseEntity<String> resp = null;
        try{
            resp = executeTimerCallBack(timerModel);
        }catch (Exception e){
            log.error("执行回调失败，抛出异常e:"+e);
        }

        //后置处理，更新Timer执行结果
        if(resp == null){
            taskModel.setStatus(TaskStatus.Failed.getStatus());
            taskModel.setOutput("resp is null");
        } else if(resp.getStatusCode().is2xxSuccessful()){
            taskModel.setStatus(TaskStatus.Succeed.getStatus());
            taskModel.setOutput(resp.toString());
        }else{
            taskModel.setStatus(TaskStatus.Failed.getStatus());
            taskModel.setOutput(resp.toString());
        }

        taskMapper.update(taskModel);
    }

    private ResponseEntity<String> executeTimerCallBack(TimerModel timerModel){
        TimerDTO timerDTO = TimerModel.objToVo(timerModel);
        NotifyHttpParam httpParam = timerDTO.getNotifyHttpParam();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;
        switch (httpParam.getMethod()){
            case "POST":
                resp = restTemplate.postForEntity(httpParam.getUrl(), httpParam.getBody(), String.class);
            default:
                log.error("不支持的httpMethod");
                break;
        }
        HttpStatus statusCode = resp.getStatusCode();
        if (!statusCode.is2xxSuccessful()){
            log.error("http 回调失败："+resp);
        }
        return resp;
    }
}
