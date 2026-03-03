package com.github.laineros.taskmanager.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    private static final Logger log = LoggerFactory.getLogger(AppProperties.class);
    @Value("${app.name:TaskManager}")
    private String appName;
    @Value("${app.max-tasks:10}")
    private int maxTasks;
    @Value("${app.default-priority:LOW}")
    private String defaultPriority;

    @PostConstruct
    public void init() {
        log.info("AppProperties загружены: appName - {}, maxTasks - {}, defaultPriority - {}",
                appName, maxTasks,  defaultPriority);
    }

    public String getAppName() {
        return appName;
    }

    public int getMaxTasks() {
        return maxTasks;
    }

    public String getDefaultPriority() {
        return defaultPriority;
    }
}
