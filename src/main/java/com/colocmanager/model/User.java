package com.colocmanager.model;

import com.colocmanager.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public class User {

    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime createdAt;

    //Relations
    private List<Task> assignedTasks;
    private List<Task> createdTasks;
    private List<Notification> notifications;
    private List<MonthlyReport> reports;
    private List<Expense> paidExpenses;
    private List<ExpenseShare> expenseShares;


    public User(String fullName, String email, String password, Role role) {
        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void addAssignedTask(Task task) {
        this.assignedTasks.add(task);
    }

    public void addCreatedTask(Task task) {
        this.createdTasks.add(task);
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void addReport(MonthlyReport report) {
        this.reports.add(report);
    }

    public void addPaidExpense(Expense expense) {
        this.paidExpenses.add(expense);
    }

    public void addExpenseShare(ExpenseShare expenseShare) {
        this.expenseShares.add(expenseShare);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public List<Task> getCreatedTasks() {
        return createdTasks;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<MonthlyReport> getReports() {
        return reports;
    }

    public List<Expense> getPaidExpenses() {
        return paidExpenses;
    }

    public List<ExpenseShare> getExpenseShares() {
        return expenseShares;
    }



}