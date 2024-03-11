package com.cclu.timer.feign;

import com.cclu.api.dto.timer.TimerDTO;
import com.cclu.api.feign.ScheduleTimerClient;
import com.cclu.timer.service.support.ScheduleTimerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ChangCheng Lu
 * @description OpenFeign提供组件内RPC接口调用
 */
@RestController
@Slf4j
public class ScheduleTimerFeignController implements ScheduleTimerClient {

    @Resource
    ScheduleTimerService scheduleTimerService;

    @Override
    public Long createTimer(TimerDTO timerDTO) {
        return scheduleTimerService.createTimer(timerDTO);
    }

    @Override
    public void enableTimer(String app, Long timerId, MultiValueMap<String, String> headers) {
        scheduleTimerService.enableTimer(app, timerId);
    }
}
