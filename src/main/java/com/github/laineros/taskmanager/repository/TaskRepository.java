package com.github.laineros.taskmanager.repository;

import com.github.laineros.taskmanager.model.Priority;
import com.github.laineros.taskmanager.model.Status;
import com.github.laineros.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;


public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task save(Task task);
    void deleteById(Long id);
    void updateStatus(Long id, Status newStatus);
    List<Task> findByPriority(Priority priority);
}
