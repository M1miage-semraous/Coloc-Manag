package com.colocmanager.service;

import com.colocmanager.enums.NotificationType;
import com.colocmanager.enums.Role;
import com.colocmanager.enums.ValidationDecision;
import com.colocmanager.model.Notification;
import com.colocmanager.model.Task;
import com.colocmanager.model.TaskValidation;
import com.colocmanager.model.User;
import com.colocmanager.repository.NotificationRepository;
import com.colocmanager.repository.TaskRepository;
import com.colocmanager.repository.TaskValidationRepository;
import com.colocmanager.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskValidationRepository taskValidationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskValidationRepository taskValidationRepository,
                       NotificationRepository notificationRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskValidationRepository = taskValidationRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public Task createTask(String title, String description, LocalDate deadline,
                           com.colocmanager.enums.ImportanceLevel importance,
                           Integer estimatedTimeHours, User assignedUser, User createdBy) {

        Task task = new Task(title, description, deadline, importance,
                estimatedTimeHours, assignedUser, createdBy);
        taskRepository.save(task);

        if (assignedUser != null) {
            Notification notification = new Notification(
                    "Nouvelle tâche assignée",
                    "La tâche '" + task.getTitle() + "' vous a été assignée.",
                    NotificationType.TASK_ASSIGNED,
                    assignedUser
            );
            notificationRepository.save(notification);
        }

        return task;
    }

    public Optional<Task> findById(UUID taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByAssignedUser(User user) {
        return taskRepository.findByAssignedUser(user);
    }

    public List<Task> getTasksCreatedBy(User user) {
        return taskRepository.findByCreatedBy(user);
    }

    public boolean startTask(UUID taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) return false;
        Task task = optionalTask.get();
        task.startTask();
        taskRepository.save(task);
        return true;
    }

    public boolean markTaskCompleted(UUID taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) return false;
        Task task = optionalTask.get();
        task.markCompleted();
        taskRepository.save(task);

        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .toList();

        for (User admin : admins) {
            Notification notification = new Notification(
                    "Tâche en attente de validation",
                    "La tâche '" + task.getTitle() + "' est terminée et attend votre validation.",
                    NotificationType.TASK_ASSIGNED,
                    admin
            );
            notificationRepository.save(notification);
        }

        return true;
    }

    public boolean validateTask(UUID taskId, User validator, String comment) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) return false;
        Task task = optionalTask.get();

        TaskValidation validation = new TaskValidation(
                ValidationDecision.APPROVED, comment, validator, task);
        taskValidationRepository.save(validation);
        task.validateTask(validation);
        taskRepository.save(task);

        if (task.getAssignedUser() != null) {
            Notification notification = new Notification(
                    "Tâche validée",
                    "Votre tâche '" + task.getTitle() + "' a été validée.",
                    NotificationType.TASK_VALIDATED,
                    task.getAssignedUser()
            );
            notificationRepository.save(notification);
        }
        return true;
    }

    public boolean rejectTask(UUID taskId, User validator, String comment) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) return false;
        Task task = optionalTask.get();

        TaskValidation validation = new TaskValidation(
                ValidationDecision.REJECTED, comment, validator, task);
        taskValidationRepository.save(validation);
        task.rejectTask(validation);
        taskRepository.save(task);

        if (task.getAssignedUser() != null) {
            Notification notification = new Notification(
                    "Tâche rejetée",
                    "Votre tâche '" + task.getTitle() + "' a été rejetée. Raison : " + comment,
                    NotificationType.TASK_REJECTED,
                    task.getAssignedUser()
            );
            notificationRepository.save(notification);
        }
        return true;
    }

    public boolean deleteTask(UUID taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) return false;
        taskRepository.delete(taskId);
        return true;
    }
}