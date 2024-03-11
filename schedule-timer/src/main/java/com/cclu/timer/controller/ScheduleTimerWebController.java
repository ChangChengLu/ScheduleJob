package com.cclu.timer.controller;

import com.cclu.api.dto.timer.TimerDTO;
import com.cclu.common.model.ResponseEntity;
import com.cclu.timer.service.support.ScheduleTimerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ChangCheng Lu
 * @description 对外提供定时器创建和激活的接口
 */
@RestController
@RequestMapping("/timer")
@Slf4j
public class ScheduleTimerWebController {

    @Resource
    private ScheduleTimerService scheduleTimerService;

    @PostMapping(value = "/createTimer")
    public ResponseEntity<Long> createTimer(@RequestBody TimerDTO timerDTO){
        Long timerId = scheduleTimerService.createTimer(timerDTO);
        return ResponseEntity.ok(timerId);
    }

    @GetMapping(value = "/enableTimer")
    public ResponseEntity<String> enableTimer(@RequestParam(value = "app") String app,
                            @RequestParam(value = "timerId") Long timerId,
                            @RequestHeader MultiValueMap<String, String> headers){
        scheduleTimerService.enableTimer(app,timerId);
        return ResponseEntity.ok("ok");
    }

}
