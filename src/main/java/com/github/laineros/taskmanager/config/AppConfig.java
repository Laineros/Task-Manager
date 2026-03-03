package com.github.laineros.taskmanager.config;

import com.github.laineros.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public String init(@Qualifier("priorityTaskRepository") TaskRepository repository) {
        log.info("Init {}", repository.getClass().getName());
        return "Initialized";
    }
}
