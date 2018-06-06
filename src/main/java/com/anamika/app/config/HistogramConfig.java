package com.anamika.app.config;

import com.anamika.app.utils.Histogram;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistogramConfig {
    @Bean
    public Histogram histogram() {
        return new Histogram(0.001, 1.0, 100);
    }
}
