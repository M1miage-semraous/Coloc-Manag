package com.colocmanager.service;

import com.colocmanager.enums.NotificationType;
import com.colocmanager.enums.ValidationDecision;
import com.colocmanager.model.Notification;
import com.colocmanager.model.Task;
import com.colocmanager.model.TaskValidation;
import com.colocmanager.model.User;
import com.colocmanager.repository.TaskRepository;
import com.colocmanager.repository.TaskValidationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskValidationRepository taskValidationRepository;

    public TaskService(TaskRepository taskRepository, TaskValidationRepository taskValidationRepository) {
        this.taskRepository = taskRepository;
        this.taskValidationRepository = taskValidationRepository;
    }

    public Task createTask(String title,
                           String description,
                           LocalDate deadline,
                           com.colocmanager.enums.ImportanceLevel importance,
                           Integer estimatedTimeHours,
                           User assignedUser,
                           User createdBy) {

        Task task = new Task(
                title,
                description,
                deadline,
                importance,
                estimatedTimeHours,
                assignedUser,
                createdBy
        );

        taskRepository.save(task);

        if (assignedUser != null) {
            assignedUser.addAssignedTask(task);

            Notification notification = new Notification(
                    "Nouvelle tâche assignée",
                    "La tâche '" + task.getTitle() + "' vous a été assignée.",
                    NotificationType.TASK_ASSIGNED,
                    assignedUser
            );

            assignedUser.addNotification(notification);
        }

        if (createdBy != null) {
            createdBy.addCreatedTask(task);
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

        if (optionalTask.isEmpty()) {
            return false;
        }

        Task task = optionalTask.get();
        task.startTask();
        return true;
    }

    public boolean markTaskCompleted(UUID taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return false;
        }

        Task task = optionalTask.get();
        task.markCompleted();
        return true;
    }

    public boolean validateTask(UUID taskId, User validator, String comment) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return false;
        }

        Task task = optionalTask.get();

        TaskValidation validation = new TaskValidation(
                ValidationDecision.APPROVED,
                comment,
                validator,
                task
        );

        taskValidationRepository.save(validation);
        task.validateTask(validation);

        if (task.getAssignedUser() != null) {
            Notification notification = new Notification(
                    "Tâche validée",
                    "Votre tâche '" + task.getTitle() + "' a été validée.",
                    NotificationType.TASK_VALIDATED,
                    task.getAssignedUser()
            );

            task.getAssignedUser().addNotification(notification);
        }

        return true;
    }

    public boolean rejectTask(UUID taskId, User validator, String comment) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return false;
        }

        Task task = optionalTask.get();

        TaskValidation validation = new TaskValidation(
                ValidationDecision.REJECTED,
                comment,
                validator,
                task
        );

        taskValidationRepository.save(validation);
        task.rejectTask(validation);

        if (task.getAssignedUser() != null) {
            Notification notification = new Notification(
                    "Tâche rejetée",
                    "Votre tâche '" + task.getTitle() + "' a été rejetée.",
                    NotificationType.TASK_REJECTED,
                    task.getAssignedUser()
            );

            task.getAssignedUser().addNotification(notification);
        }

        return true;
    }

    public boolean deleteTask(UUID taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isEmpty()) {
            return false;
        }

        taskRepository.delete(taskId);
        return true;
    }
}