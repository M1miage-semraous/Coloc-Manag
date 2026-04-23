package com.colocmanager.model;

import com.colocmanager.enums.TaskStatus;
import java.util.List;
import java.util.UUID;

public class MonthlyReport {


    private UUID id;
    private int month;
    private int year;
    private int totalTasksAssigned;
    private int totalTasksValidated;
    private int totalTasksRejected;
    private double totalPaid;
    private double totalDue;
    private User owner;
    private List<Task> tasks;
    private List<Expense> expenses;


    public MonthlyReport(int month, int year, User owner, List<Task> tasks, List<Expense> expenses) {
        this.id = UUID.randomUUID();
        this.month = month;
        this.year = year;
        this.owner = owner;
        this.tasks = tasks;
        this.expenses = expenses;
        generate(); // calcul automatique de tous les totaux à la création
    }


    public void generate() {
        this.totalTasksAssigned = tasks.size();

        this.totalTasksValidated = (int) tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.VALIDATED)
                .count();

        this.totalTasksRejected = (int) tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.REJECTED)
                .count();

        // Total déjà payé par l'utilisateur sur ses parts
        this.totalPaid = expenses.stream()
                .flatMap(e -> e.getShares().stream())
                .filter(s -> s.getUser() != null
                        && s.getUser().getId().equals(owner.getId())
                        && s.isPaid())
                .mapToDouble(ExpenseShare::getAmountDue)
                .sum();

        // Total restant à payer par l'utilisateur
        this.totalDue = expenses.stream()
                .flatMap(e -> e.getShares().stream())
                .filter(s -> s.getUser() != null
                        && s.getUser().getId().equals(owner.getId())
                        && !s.isPaid())
                .mapToDouble(ExpenseShare::getAmountDue)
                .sum();
    }

    public UUID getId() { return id; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public int getTotalTasksAssigned() { return totalTasksAssigned; }
    public int getTotalTasksValidated() { return totalTasksValidated; }
    public int getTotalTasksRejected() { return totalTasksRejected; }
    public double getTotalPaid() { return totalPaid; }
    public double getTotalDue() { return totalDue; }
    public User getOwner() { return owner; }

    @Override
    public String toString() {
        return "MonthlyReport{month=" + month + "/" + year +
                ", tasksAssigned=" + totalTasksAssigned +
                ", validated=" + totalTasksValidated +
                ", rejected=" + totalTasksRejected +
                ", totalPaid=" + totalPaid +
                ", totalDue=" + totalDue + "}";
    }
}












