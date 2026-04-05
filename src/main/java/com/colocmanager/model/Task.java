package com.colocmanager.model;

import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.PriorityLevel;
import com.colocmanager.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {

    private UUID id;
    private String title;
    private LocalDateTime validatedAt;
    private LocalDate deadline;
    private ImportanceLevel importance;
    private int estimatedTimeHours;
    private PriorityLevel calculatedPriority;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private String description;
    private int month;
    private int year;

    public int getMonth() { return month; }
    public int getYear() { return year; }

    private User assignedUser;
    private User createdBy;
    private List<Expense> relatedExpenses;
    private TaskValidation validation;

    public Task(String title, String description, LocalDate deadline,
                ImportanceLevel importance, Integer estimatedTimeHours,
                User assignedUser, User createdBy) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = TaskStatus.TO_DO;
        this.relatedExpenses = new ArrayList<>();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.importance = importance;
        this.estimatedTimeHours = estimatedTimeHours;
        this.assignedUser = assignedUser;
        this.createdBy = createdBy;
        this.calculatedPriority = calculatePriority();
    }

    public PriorityLevel calculatePriority() {
        if (importance == ImportanceLevel.HIGH && estimatedTimeHours >= 4) {
            this.calculatedPriority = PriorityLevel.URGENT;
        } else if (importance == ImportanceLevel.HIGH) {
            this.calculatedPriority = PriorityLevel.HIGH;
        } else if (importance == ImportanceLevel.MEDIUM) {
            this.calculatedPriority = PriorityLevel.MEDIUM;
        } else {
            this.calculatedPriority = PriorityLevel.LOW;
        }
        return this.calculatedPriority;
    }

    public void markCompleted() {
        this.status = TaskStatus.PENDING_VALIDATION;
    }

    public void startTask() {
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void validateTask(TaskValidation validation) {
        this.validation = validation;
        this.status = TaskStatus.VALIDATED;
    }

    public void rejectTask(TaskValidation validation) {
        this.validation = validation;
        this.status = TaskStatus.REJECTED;
    }

    public void addRelatedExpense(Expense expense) {
        this.relatedExpenses.add(expense);
    }

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public ImportanceLevel getImportance() { return importance; }
    public void setImportance(ImportanceLevel importance) { this.importance = importance; }

    public Integer getEstimatedTimeHours() { return estimatedTimeHours; }
    public void setEstimatedTimeHours(Integer estimatedTimeHours) { this.estimatedTimeHours = estimatedTimeHours; }

    public PriorityLevel getCalculatedPriority() { return calculatedPriority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getAssignedUser() { return assignedUser; }
    public void setAssignedUser(User assignedUser) { this.assignedUser = assignedUser; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<Expense> getRelatedExpenses() { return relatedExpenses; }

    public TaskValidation getValidation() { return validation; }
    public void setValidation(TaskValidation validation) { this.validation = validation; }
}