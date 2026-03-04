package com.github.laineros.taskmanager.service;

import com.github.laineros.taskmanager.config.AppProperties;
import com.github.laineros.taskmanager.model.Priority;
import com.github.laineros.taskmanager.exception.TaskNotFoundException;
import com.github.laineros.taskmanager.model.Status;
import com.github.laineros.taskmanager.model.Task;
import com.github.laineros.taskmanager.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final ObjectProvider<TaskStatsService> statsServiceProvider;
    private final AppProperties appProperties;

    public TaskServiceImpl(TaskRepository taskRepository,
                           ObjectProvider<TaskStatsService> statsServiceProvider,
                           AppProperties appProperties) {
        this.taskRepository = taskRepository;
        this.statsServiceProvider = statsServiceProvider;
        this.appProperties = appProperties;
    }

    @PostConstruct
    public void init() {
        log.info("TaskService инициализирован");
    }

    @PreDestroy
    public void destroy() {
        log.info("Завершение работы. Задач в хранилище: {}", taskRepository.findAll().size());
    }

    @Override
    public Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }

        if (task.getPriority() == null) {
            Priority defaultPriority = Priority.valueOf(appProperties.getDefaultPriority());
            task.setPriority(defaultPriority);
        }

        int currentSize = taskRepository.findAll().size();
        if (currentSize >= appProperties.getMaxTasks()) {
            throw new IllegalStateException(
                    "Превышен лимит задач: " + appProperties.getMaxTasks()
            );
        }

        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAll(String status, String priority) {
        List<Task> tasks = taskRepository.findAll();

        if (status != null) {
            Status statusEnum = Status.valueOf(status);
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == statusEnum)
                    .toList();
        }

        if (priority != null) {
            Priority priorityEnum = Priority.valueOf(priority);
            tasks = tasks.stream()
                    .filter(t -> t.getPriority() == priorityEnum)
                    .toList();
        }

        return tasks;
    }

    @Override
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с id=" + id + " не найдена"));
    }

    @Override
    public Task update(Long id, Task task) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Задача с id=" + id + " не найдена"));

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }

        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setPriority(task.getPriority() != null ? task.getPriority() : existing.getPriority());
        existing.setStatus(task.getStatus() != null ? task.getStatus() : existing.getStatus());

        return taskRepository.save(existing);
    }

    @Override
    public Task updateStatus(Long id, String status) {
        Status statusEnum = Status.valueOf(status);
        Task task = getById(id);
        task.setStatus(statusEnum);
        return taskRepository.save(task);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Map<String, Long> getStats() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(t -> t.getStatus().name(), Collectors.counting()));
    }

    public void demonstratePrototype() {
        TaskStatsService stats1 = statsServiceProvider.getObject();
        TaskStatsService stats2 = statsServiceProvider.getObject();

        System.out.println("UUID 1: " + stats1.getInstanceId());
        System.out.println("UUID 2: " + stats2.getInstanceId());
        System.out.println("UUID одинаковые? " + stats1.getInstanceId().equals(stats2.getInstanceId()));
    }
}
