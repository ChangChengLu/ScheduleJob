package com.cclu.timer.service.impl;

import com.cclu.api.dto.timer.TimerDTO;
import com.cclu.common.redis.ReentrantDistributeLock;
import com.cclu.timer.enums.TimerStatus;
import com.cclu.timer.exception.BusinessException;
import com.cclu.timer.exception.ErrorCode;
import com.cclu.timer.manager.MigratorManager;
import com.cclu.timer.mapper.TimerMapper;
import com.cclu.timer.model.TimerModel;
import com.cclu.timer.service.ScheduleTimerService;
import com.cclu.timer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ScheduleTimerServiceImpl implements ScheduleTimerService {

    @Autowired
    private TimerMapper timerMapper;

    @Autowired
    ReentrantDistributeLock reentrantDistributeLock;

    @Autowired
    MigratorManager migratorManager;

    private static final int  defaultGapSeconds= 3;

    @Override
    public Long createTimer(TimerDTO timerDTO) {
//        String lockToken = TimerUtils.GetTokenStr();
////        // 只加锁不解锁，只有超时解锁；超时时间控制频率；
////        boolean ok = reentrantDistributeLock.lock(
////                TimerUtils.GetCreateLockKey(timerDTO.getApp()),
////                lockToken,
////                defaultGapSeconds);
////        if(!ok){
////            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建/删除操作过于频繁，请稍后再试！");
////        }

        boolean isValidCron = CronExpression.isValidExpression(timerDTO.getCron());
        if(!isValidCron){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"invalid cron");
        }

        TimerModel timerModel = TimerModel.voToObj(timerDTO);
        if (timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        timerMapper.save(timerModel);
        return timerModel.getTimerId();
    }

    @Override
    public void deleteTimer(String app, long id) {
        String lockToken = TimerUtils.GetTokenStr();
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.GetCreateLockKey(app),
                lockToken,
                defaultGapSeconds);
        if(!ok){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建/删除操作过于频繁，请稍后再试！");
        }

        timerMapper.deleteById(id);
    }

    @Override
    public void update(TimerDTO timerDTO) {
        TimerModel timerModel = TimerModel.voToObj(timerDTO);
        if (timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        timerMapper.update(timerModel);
    }

    @Override
    public TimerDTO getTimer(String app, long id) {
        TimerModel timerModel  = timerMapper.getTimerById(id);
        TimerDTO timerDTO = TimerModel.objToVo(timerModel);
        return timerDTO;
    }

    @Override
    public void enableTimer(String app, long id) {
        String lockToken = TimerUtils.GetTokenStr();
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.GetEnableLockKey(app),
                lockToken,
                defaultGapSeconds);
        if(!ok){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"激活/去激活操作过于频繁，请稍后再试！");
        }

        // 激活逻辑
        doEnableTimer(id);
    }

    @Transactional
    public void doEnableTimer(long id){
        // 1. 数据库获取Timer
        TimerModel timerModel = timerMapper.getTimerById(id);
        if(timerModel == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"激活失败，timer不存在：timerId"+id);
        }
        // 2. 校验状态
        if(timerModel.getStatus() == TimerStatus.Enable.getStatus()){
            log.warn("Timer非Unable状态，激活失败，timerId:"+timerModel.getTimerId());
        }
        // 修改 timer 状态为激活态
        timerModel.setStatus(TimerStatus.Enable.getStatus());
        timerMapper.update(timerModel);
        //迁移数据
        migratorManager.migrateTimer(timerModel);

    }


    @Override
    public void unEnableTimer(String app, long id) {
        String lockToken = TimerUtils.GetTokenStr();
        boolean ok = reentrantDistributeLock.lock(
                TimerUtils.GetEnableLockKey(app),
                lockToken,
                defaultGapSeconds);
        if(!ok){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"激活/去激活操作过于频繁，请稍后再试！");
        }

        // 去激活逻辑
        doUnEnableTimer(id);
    }

    @Transactional
    public void doUnEnableTimer(Long id){
        // 1. 数据库获取Timer
        TimerModel timerModel = timerMapper.getTimerById(id);
        // 2. 校验状态
        if(timerModel.getStatus() != TimerStatus.Unable.getStatus()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Timer非Unable状态，去激活失败，id:"+id);
        }
        timerModel.setStatus(TimerStatus.Unable.getStatus());
        timerMapper.update(timerModel);
    }


    @Override
    public List<TimerDTO> getAppTimers(String app) {
        return null;
    }
}
