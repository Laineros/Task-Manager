package com.github.laineros.taskmanager.runner;

import com.github.laineros.taskmanager.config.AppProperties;
import com.github.laineros.taskmanager.model.Priority;
import com.github.laineros.taskmanager.model.Status;
import com.github.laineros.taskmanager.model.Task;
import com.github.laineros.taskmanager.repository.TaskRepository;
import com.github.laineros.taskmanager.service.TaskService;
import com.github.laineros.taskmanager.service.TaskServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("cli")
public class AppRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AppRunner.class);

    private final AppProperties appProperties;
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final ApplicationContext applicationContext;

    public AppRunner(AppProperties appProperties,
                     TaskService taskService,
                     TaskRepository taskRepository,
                     ApplicationContext applicationContext) {
        this.appProperties = appProperties;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        // Шаг 1: Приветствие
        log.info("\nШаг 1  Приветствие");
        log.info("Добро пожаловать в {}! Лимит: {}. Приоритет по умолчанию: {}",
                appProperties.getAppName(), appProperties.getMaxTasks(), appProperties.getDefaultPriority());

        // Шаг 2: Добавление задач
        log.info("\nШаг 2  Добавление задач");
        Task task1 = new Task(null, "task1", "desc 1", Priority.LOW, Status.NEW);
        Task task2 = new Task(null, "task2", "desc 2", Priority.MEDIUM, Status.NEW);
        Task task3 = new Task(null, "task3", "desc 3", Priority.HIGH, Status.NEW);

        taskService.createTask(task1);
        taskService.createTask(task2);
        taskService.createTask(task3);

        log.info("Созданные задачи:");
        taskRepository.findAll().forEach(t ->
                log.info("id={}, title={}, priority={}, status={}",
                        t.getId(), t.getTitle(), t.getPriority(), t.getStatus()));

        // Шаг 4: Изменение статусов
        log.info("Шаг 4  Изменение статусов");
        List<Task> tasks = taskRepository.findAll();
        if (tasks.size() >= 2) {
            taskRepository.updateStatus(tasks.get(0).getId(), Status.IN_PROGRESS);
            taskRepository.updateStatus(tasks.get(1).getId(), Status.DONE);
        }

        System.out.println("DONE задачи:");
        taskRepository.findAll().stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .forEach(t -> log.info("id={}, title={}", t.getId(), t.getTitle()));

        System.out.println("HIGH задачи:");
        taskRepository.findByPriority(Priority.HIGH)
                .forEach(t -> log.info("id={}, title={}", t.getId(), t.getTitle()));

        // Шаг 5: Prototype и ObjectProvider
        log.info("\nШаг 5  Prototype и ObjectProvider");
        if (taskService instanceof TaskServiceImpl) {
            ((TaskServiceImpl) taskService).demonstratePrototype();
        }

        // Шаг 6: ApplicationContext
        log.info("\nШаг 6  ApplicationContext");
        TaskRepository manuallyGetRepo = applicationContext.getBean(TaskRepository.class);
        log.info("Ручное получение бина: {}", manuallyGetRepo.getClass().getSimpleName());

        log.info("Всего бинов в контексте: {}", applicationContext.getBeanDefinitionCount());

        log.info("Бины содержащие 'task' (без учета регистра):");
        Arrays.stream(applicationContext.getBeanDefinitionNames())
                .filter(name -> name.toLowerCase().contains("task"))
                .forEach(System.out::println);

        // Шаг 3: Граничные случаи (после демонстрации остального)
        log.info("\nШаг 3  Граничные случаи");
        try {
            // Пустой title
            Task invalidTask = new Task(null, "", "Описание", Priority.LOW, Status.NEW);
            taskService.createTask(invalidTask);
        } catch (Exception e) {
            log.warn("Исключение при пустом title: {}", e.getMessage());
        }

        try {
            // Превышение лимита
            for (int i = 0; i < appProperties.getMaxTasks() + 1; i++) {
                Task extraTask = new Task(null, "Задача " + i, "Описание", Priority.LOW, Status.NEW);
                taskService.createTask(extraTask);
            }
        } catch (Exception e) {
            log.warn("Исключение при превышении лимита: {}", e.getMessage());
        }

        // Шаг 7: Информация о профиле
        log.info("\nШаг 7  Переключение профиля");
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        log.info("Активный профиль: {}", activeProfiles.length > 0 ? activeProfiles[0] : "default");

        if (activeProfiles.length > 0 && "prod".equals(activeProfiles[0])) {
            log.info("Задачи отсортированы по приоритету (уже показано в шаге 2)");
        }
    }
}