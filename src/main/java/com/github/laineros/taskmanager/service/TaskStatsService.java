package com.github.laineros.taskmanager.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class TaskStatsService {
    private final String instanceId;

    public TaskStatsService() {
        this.instanceId = UUID.randomUUID().toString();
        System.out.println("Создан новый экземпляр TaskStatsService с UUID: " + instanceId);
    }

    public String getStats() {
        return "Stats instance: " + instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

}
