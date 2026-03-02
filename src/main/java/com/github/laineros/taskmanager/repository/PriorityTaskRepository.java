package com.github.laineros.taskmanager.repository;

import com.github.laineros.taskmanager.model.Priority;
import com.github.laineros.taskmanager.model.Status;
import com.github.laineros.taskmanager.model.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class PriorityTaskRepository implements TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    private static final List<Priority> PRIORITY_ORDER = Arrays.asList(
            Priority.HIGH, Priority.MEDIUM, Priority.LOW
    );

    private final Comparator<Task> priorityComparator = Comparator.comparingInt(
            task -> PRIORITY_ORDER.indexOf(task.getPriority())
    );

    public List<Task> getTaskByPriority(Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll() {
        return tasks.stream()
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public Task save(Task task) {
        if(task.getId() == null) {
            task.setId(currentId.getAndIncrement());
            tasks.add(task);
        } else {
            deleteById(task.getId());
            tasks.add(task);
        }
        return task;
    }

    @Override
    public void deleteById(Long id) {
        tasks.removeIf(task -> task.getId().equals(id));
    }

    @Override
    public void updateStatus(Long id, Status newStatus) {
        findById(id).ifPresent(task -> task.setStatus(newStatus));
    }

    @Override
    public List<Task> findByPriority(Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }
}
