package com.cclu.timer.manager;

import com.cclu.timer.model.TimerModel;

/**
 * @author ChangCheng Lu
 */
public interface MigratorManager{

    /**
     * 将 Timer 从 MySQL 迁移至 Redis
     * @param timerModel 待迁移的TimerModel
     */
    void migrateTimer(TimerModel timerModel);

}
