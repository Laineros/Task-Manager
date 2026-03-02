package com.github.laineros.taskmanager.repository;

import com.github.laineros.taskmanager.model.Priority;
import com.github.laineros.taskmanager.model.Status;
import com.github.laineros.taskmanager.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
@Profile("dev")
@Primary
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Task save(Task task) {
        if(task.getId() == null) {
            task.setId(currentId.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        tasks.remove(id);
    }

    @Override
    public void updateStatus(Long id, Status newStatus) {
        findById(id).ifPresent(task -> task.setStatus(newStatus));
    }

    @Override
    public List<Task> findByPriority(Priority priority) {
        return tasks.values().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }
}
