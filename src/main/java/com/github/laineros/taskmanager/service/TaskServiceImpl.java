package com.github.laineros.taskmanager.service;

import com.github.laineros.taskmanager.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        log.info("TaskService инициализирован");
    }

    @PreDestroy
    public void destroy() {
        log.info("Завершение работы. Задач в хранилище: {}", taskRepository.findAll().size());
    }

}
