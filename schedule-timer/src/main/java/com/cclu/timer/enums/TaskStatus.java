package com.cclu.timer.enums;

/**
 * @author ChangCheng Lu
 * @description 任务执行状态
 */
public enum TaskStatus {
    /**
     * 0: 未运行
     * 1: 执行中
     * 2: 执行成功
     * 3: 执行失败
     */
    NotRun(0),
    Running(1),
    Succeed(2),
    Failed(3);

    private final int status;

    TaskStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
