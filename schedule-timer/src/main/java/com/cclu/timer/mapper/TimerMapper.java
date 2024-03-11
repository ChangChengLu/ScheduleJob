package com.cclu.timer.mapper;


import com.cclu.timer.model.TimerModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ChangCheng Lu
 */
@Mapper
public interface TimerMapper {

    /**
     * 保存timerModel
     *
     * @param timerModel timerModel
     */
    void save(@Param("timerModel") TimerModel timerModel);

    /**
     * 根据timerId删除TimerModel
     *
     * @param timerId timerId
     */
    void deleteById(@Param("timerId") Long timerId);

    /**
     * 更新TimerModel
     *
     * @param timerModel timerModel
     */
    void update(@Param("timerModel") TimerModel timerModel);

    /**
     * 根据timerId查询TimerModel
     *
     * @param timerId timerId
     * @return TimerModel
     */
    TimerModel getTimerById(@Param("timerId") Long timerId);

    /**
     * 根据status查询TimerModel
     *
     * @param status      定时器状态
     * @return TimerModel
     */
    List<TimerModel> getTimersByStatus(@Param("status") int status);
}
