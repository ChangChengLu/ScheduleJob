package com.cclu.timer.feign;

import com.cclu.api.dto.timer.TimerDTO;
import com.cclu.api.feign.ScheduleTimerClient;
import com.cclu.timer.service.ScheduleTimerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ScheduleTimerFeignController implements ScheduleTimerClient {

    @Autowired
    ScheduleTimerService xTimerService;

    @Override
    public Long createTimer(TimerDTO timerDTO) {
        return xTimerService.createTimer(timerDTO);
    }

    @Override
    public void enableTimer(String app, Long timerId, MultiValueMap<String, String> headers) {
        xTimerService.enableTimer(app, timerId);
    }
}
