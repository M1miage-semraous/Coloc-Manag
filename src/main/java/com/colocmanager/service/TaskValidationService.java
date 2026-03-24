package com.colocmanager.service;

import com.colocmanager.enums.NotificationType;
import com.colocmanager.enums.ValidationDecision;
import com.colocmanager.model.Notification;
import com.colocmanager.model.Task;
import com.colocmanager.model.TaskValidation;
import com.colocmanager.model.User;
import com.colocmanager.repository.TaskRepository;
import com.colocmanager.repository.TaskValidationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskValidationService {

    private final TaskValidationRepository taskValidationRepository;
    private final TaskRepository taskRepository;

    public TaskValidationService(TaskValidationRepository taskValidationRepository,
                                 TaskRepository taskRepository) {
        this.taskValidationRepository = taskValidationRepository;
        this.taskRepository = taskRepository;
    }

    public Optional<TaskValidation> findById(UUID validationId) {
        return taskValidationRepository.findById(validationId);
    }

    public List<TaskValidation> getAllValidations() {
        return taskValidationRepository.findAll();
    }

    public Optional<TaskValidation> getValidationByTaskId(UUID taskId) {
        return taskValidationRepository.findByTaskId(taskId);
    }

    public List<TaskValidation> getValidationsByValidator(UUID validatorId) {
        return taskValidationRepository.findByValidatorId(validatorId);
    }

    public Optional<TaskValidation> validateTask(UUID taskId, User validator, String comment) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isEmpty() || validator == null) {
            return Optional.empty();
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

        return Optional.of(validation);
    }

    public Optional<TaskValidation> rejectTask(UUID taskId, User validator, String comment) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalTask.isEmpty() || validator == null) {
            return Optional.empty();
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

        return Optional.of(validation);
    }

    public boolean deleteValidation(UUID validationId) {
        Optional<TaskValidation> optionalValidation = taskValidationRepository.findById(validationId);

        if (optionalValidation.isEmpty()) {
            return false;
        }

        taskValidationRepository.delete(validationId);
        return true;
    }
}