package com.github.laineros.taskmanager.service;

import com.github.laineros.taskmanager.model.Task;

import java.util.List;
import java.util.Map;

public interface TaskService {
    Task createTask(Task task);

    List<Task> getAll(String status, String priority);

    Task getById(Long id);

    Task update(Long id, Task task);

    Task updateStatus(Long id, String status);

    void delete(Long id);

    Map<String, Long> getStats();
}
