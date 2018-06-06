package com.anamika.app.component;

import com.anamika.app.service.MissionMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * {@link AppRunner} starts the Monitor Mission background task
 */
@Component
public class AppRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MissionMonitoringService missionMonitoringService;

    @Autowired
    public AppRunner(MissionMonitoringService missionMonitoringService) {
        this.missionMonitoringService = missionMonitoringService;
    }

    @Override
    public void run(String... strings) throws Exception {
        LOGGER.info("AppRunner Starting");
        missionMonitoringService.doAsync();
        LOGGER.info("AppRunner Ending");
    }
}
