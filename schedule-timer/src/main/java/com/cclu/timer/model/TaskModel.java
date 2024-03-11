package com.cclu.timer.model;

import com.cclu.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ChangCheng Lu
 * @description 任务模型
 */
@Getter
@Setter
public class TaskModel extends BaseModel implements Serializable {

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 业务/应用标识
     */
    private String app;

    /**
     * 关联定时器
     */
    private Long timerId;

    /**
     * 任务输出结果
     */
    private String output;

    /**
     * 运行时间
     */
    private Long runTimer;

    /**
     * 执行耗时
     */
    private int costTime;

    /**
     * 定时器状态：
     *  1-未激活
     *  2-激活
     */
    private int status;

}
