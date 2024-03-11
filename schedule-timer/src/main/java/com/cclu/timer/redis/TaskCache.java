package com.cclu.timer.redis;

import com.cclu.timer.common.conf.SchedulerAppConf;
import com.cclu.timer.exception.BusinessException;
import com.cclu.timer.exception.ErrorCode;
import com.cclu.timer.model.TaskModel;
import com.cclu.timer.utils.TimerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author ChangCheng Lu
 * @description Redis缓存工具类
 */
@Component
@Slf4j
public class TaskCache {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    SchedulerAppConf schedulerAppConf;

    /**
     * 返回Redis的Key
     * @param taskModel taskModel
     * @return keyName/tableName
     */
    public String getTableName(TaskModel taskModel){
        int maxBucket = schedulerAppConf.getBucketsNum();

        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String timeStr = sdf.format(new Date(taskModel.getRunTimer()));
        long index = taskModel.getTimerId() % maxBucket;
        return sb.append(timeStr).append("_").append(index).toString();
    }

    /**
     * 开启 Redis 事务，批量打点任务
     * @param taskList taskList
     * @return         是否成功
     */
    public boolean cacheSaveTasks(List<TaskModel> taskList){

        try {
            SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    redisOperations.multi();
                    for (TaskModel task : taskList) {
                        long unix = task.getRunTimer();
                        String tableName = getTableName(task);
                        redisTemplate.opsForZSet().add(
                                tableName,
                                TimerUtils.unionTimerIdUnix(task.getTimerId(), unix),
                                unix);
                    }
                    return redisOperations.exec(); //2023-11-06 21:54_1
                }
            };
            redisTemplate.execute(sessionCallback);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从 Redis 缓存中，获取已经打点的任务
     * @param key   keyName/tableName
     * @param start 开始时间
     * @param end   结束时间
     * @return      任务模型列表
     */
    public List<TaskModel> getTasksFromCache(String key, long start, long end){
        List<TaskModel> tasks = new ArrayList<>();

        Set<Object> timerIdUnixSet = redisTemplate.opsForZSet().rangeByScore(key, start, end-1);
        if(CollectionUtils.isEmpty(timerIdUnixSet)){
            return tasks;
        }

        for (Object timerIdUnixObj : timerIdUnixSet) {
            TaskModel task = new TaskModel();
            String timerIdUnix = (String) timerIdUnixObj;
            List<Long> longSet = TimerUtils.splitTimerIdUnix(timerIdUnix);
            if(longSet.size() != 2){
                log.error("splitTimerIDUnix 错误, timerIdUnix: "+ timerIdUnix);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"splitTimerIDUnix 错误, timerIdUnix: " + timerIdUnix);
            }
            // timerId
            task.setTimerId(longSet.get(0));
            // runtime
            task.setRunTimer(longSet.get(1));
            tasks.add(task);
        }

        return tasks;
    }

}
