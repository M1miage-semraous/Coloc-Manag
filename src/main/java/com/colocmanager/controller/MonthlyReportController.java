package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.model.Expense;
import com.colocmanager.model.MonthlyReport;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;

import java.time.LocalDate;
import java.util.List;

public class MonthlyReportController {

    private final User currentUser;

    public MonthlyReportController(User currentUser) {
        this.currentUser = currentUser;
    }

    public MonthlyReport generateReport(int month, int year) {
        List<Task> tasks = MainApp.taskService.getAllTasks().stream()
                .filter(t -> t.getCreatedAt() != null
                        && t.getCreatedAt().getMonthValue() == month
                        && t.getCreatedAt().getYear() == year)
                .toList();

        List<Expense> expenses = MainApp.expenseService.getExpensesByMonth(month, year);

        return new MonthlyReport(month, year, currentUser, tasks, expenses);
    }

    public int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }
}