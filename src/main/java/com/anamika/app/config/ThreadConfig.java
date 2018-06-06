package com.anamika.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.invoke.MethodHandles;

/**
 * {@link ThreadConfig} class creates Thread Pool ({@link ThreadPoolTaskExecutor}) to run the Monitor Mission background task
 * The Monitor Mission background task is started by {@link com.anamika.app.component.AppRunner} Class
 */
@Configuration
public class ThreadConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /*
        Create Thread Pool to run Background Monitoring Task
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(2);
        pool.setMaxPoolSize(2);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.afterPropertiesSet();
        return pool;
    }
}
