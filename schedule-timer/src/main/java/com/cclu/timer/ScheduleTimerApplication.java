package com.cclu.timer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ChangCheng Lu
 */
@SpringBootApplication(scanBasePackages = {"com.cclu"})
@EnableScheduling
@EnableAsync
public class ScheduleTimerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleTimerApplication.class, args);
    }
}
