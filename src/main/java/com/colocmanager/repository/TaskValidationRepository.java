package com.colocmanager.repository;

import com.colocmanager.model.TaskValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskValidationRepository {

    private final List<TaskValidation> validations = new ArrayList<>();

    public void save(TaskValidation validation) {
        if (validation == null || validation.getTask() == null || validation.getTask().getId() == null) {
            return;
        }

        Optional<TaskValidation> existing = findByTaskId(validation.getTask().getId());
        existing.ifPresent(validations::remove);

        validations.add(validation);
    }

    public List<TaskValidation> findAll() {
        return new ArrayList<>(validations);
    }

    public Optional<TaskValidation> findById(UUID id) {
        return validations.stream()
                .filter(v -> v.getId() != null && v.getId().equals(id))
                .findFirst();
    }

    public Optional<TaskValidation> findByTaskId(UUID taskId) {
        return validations.stream()
                .filter(v -> v.getTask() != null
                        && v.getTask().getId() != null
                        && v.getTask().getId().equals(taskId))
                .findFirst();
    }

    public List<TaskValidation> findByValidatorId(UUID validatorId) {
        List<TaskValidation> result = new ArrayList<>();

        for (TaskValidation validation : validations) {
            if (validation.getValidator() != null
                    && validation.getValidator().getId() != null
                    && validation.getValidator().getId().equals(validatorId)) {
                result.add(validation);
            }
        }

        return result;
    }

    public void delete(UUID id) {
        validations.removeIf(v -> v.getId() != null && v.getId().equals(id));
    }
}