package com.colocmanager.service;

import com.colocmanager.model.Expense;
import com.colocmanager.model.MonthlyReport;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import com.colocmanager.repository.ExpenseRepository;
import com.colocmanager.repository.MonthlyReportRepository;
import com.colocmanager.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MonthlyReportService {

    private MonthlyReportRepository monthlyReportRepository;
    private TaskRepository taskRepository;
    private ExpenseRepository expenseRepository;

    public MonthlyReportService(MonthlyReportRepository monthlyReportRepository, TaskRepository taskRepository, ExpenseRepository expenseRepository) {
        this.monthlyReportRepository = monthlyReportRepository;
        this.taskRepository = taskRepository;
        this.expenseRepository = expenseRepository;
    }

    public MonthlyReport generateReport(User owner, int month, int year) {
        List<Task> tasks = taskRepository.findByMonth(month, year);
        List<Expense> expenses = expenseRepository.findByMonth(month, year);
        MonthlyReport report = new MonthlyReport(month, year, owner, tasks, expenses);
        monthlyReportRepository.save(report);
        return report;
    }

    public List<MonthlyReport> getAllReports() {
        return monthlyReportRepository.findAll();
    }

    public Optional<MonthlyReport> getReportById(UUID id) {
        return monthlyReportRepository.findById(id);
    }

    public List<MonthlyReport> getReportsByOwner(UUID userId) {
        return monthlyReportRepository.findByOwner(userId);
    }

    public Optional<MonthlyReport> getReportByOwnerAndMonth(UUID userId, int month, int year) {
        return monthlyReportRepository.findByOwnerAndMonth(userId, month, year);
    }

    public List<MonthlyReport> getReportsByMonth(int month, int year) {
        return monthlyReportRepository.findByMonth(month, year);
    }

    public void deleteReport(UUID id) {
        monthlyReportRepository.delete(id);
    }
}