package com.cclu.timer.common.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author ChangCheng Lu
 * @description 迁移器模块配置
 */
@Getter
@Setter
@Component
public class MigratorAppConf {

    @Value("${migrator.workersNum}")
    private int workersNum;

    @Value("${migrator.migrateStepMinutes}")
    private int migrateStepMinutes;

    @Value("${migrator.migrateSuccessExpireMinutes}")
    private int migrateSuccessExpireMinutes;

    @Value("${migrator.migrateTryLockMinutes}")
    private int migrateTryLockMinutes;

    @Value("${migrator.timerDetailCacheMinutes}")
    private int timerDetailCacheMinutes;

}
