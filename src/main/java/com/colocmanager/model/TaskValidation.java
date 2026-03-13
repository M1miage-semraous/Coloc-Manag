package com.colocmanager.model;

import com.colocmanager.enums.ValidationDecision;

import java.time.LocalDateTime;
import java.util.UUID;
import  java.time.LocalDateTime;

public class TaskValidation {

    private UUID id;
    private  ValidationDecision decision;
    private String comment;
    private LocalDateTime validatedAt;

    private User validator;
    private Task task;

    public TaskValidation() {
        this.id = UUID.randomUUID();
        this.validatedAt = LocalDateTime.now();
    }

    public TaskValidation(ValidationDecision decision, String comment, User validator, Task task) {
        this();
        this.decision = decision;
        this.comment = comment;
        this.validator = validator;
        this.task = task;
    }

    public UUID getId() {
        return id;
    }

    public ValidationDecision getDecision() {
        return decision;
    }

    public void setDecision(ValidationDecision decision) {
        this.decision = decision;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getValidator() {
        return validator;
    }

    public void setValidator(User validator) {
        this.validator = validator;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }


}












