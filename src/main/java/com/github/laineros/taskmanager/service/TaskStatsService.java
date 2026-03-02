package com.github.laineros.taskmanager.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Scope("prototype")
public class TaskStatsService {
    private final List<UUID> tasks = new ArrayList<>();
}
