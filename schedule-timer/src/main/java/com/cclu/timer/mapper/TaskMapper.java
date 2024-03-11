package com.cclu.timer.mapper;


import com.cclu.timer.model.TaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ChangCheng Lu
 */
@Mapper
public interface TaskMapper {

    /**
     * 批量插入taskModel
     *
     * @param taskList taskList
     */
    void batchSave(@Param("taskList") List<TaskModel> taskList);

    /**
     * 根据timerId删除taskModel
     *
     * @param taskId taskId
     */
    void deleteById(@Param("taskId") Long taskId);

    /**
     * 更新TimerModel
     *
     * @param taskModel taskModel
     */
    void update(@Param("taskModel") TaskModel taskModel);

    /**
     * 根据taskId查询Task
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param taskStatus 任务状态
     * @return TaskModel
     */
    List<TaskModel> getTasksByTimeRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("taskStatus") int taskStatus);

    /**
     * 根据taskId查询Task
     *
     * @param timerId  定时器
     * @param runTimer 执行时间
     * @return TaskModel
     */
    TaskModel getTasksByTimerIdUnix(@Param("timerId") Long timerId, @Param("runTimer") Long runTimer);

}
