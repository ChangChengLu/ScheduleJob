package com.cclu.timer.enums;

/**
 * @author ChangCheng Lu
 * @description 定时器状态
 */
public enum TimerStatus {
    /**
     * 1: 未激活
     * 2: 激活
     */
    Unable(1),
    Enable(2),;

    private final int status;

    TimerStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public static TimerStatus getTimerStatus(int status){
        for (TimerStatus value:TimerStatus.values()) {
            if(value.status == status){
                return value;
            }
        }
        return null;
    }
}
