package com.cclu.timer.service;

import com.cclu.api.dto.timer.TimerDTO;

import java.util.List;

public interface ScheduleTimerService {

    Long createTimer(TimerDTO timerDTO);

    void deleteTimer(String app, long id);

    void update(TimerDTO timerDTO);

    TimerDTO getTimer(String app, long id);

    void enableTimer(String app, long id);

    void unEnableTimer(String app, long id);

    List<TimerDTO> getAppTimers(String app);
}
