package com.colocmanager.service;

import com.colocmanager.model.Expense;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;
import com.colocmanager.repository.ExpenseRepository;
import com.colocmanager.repository.ExpenseShareRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseShareRepository expenseShareRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseShareRepository = expenseShareRepository;
    }

    public Expense createExpense(String label, Double amount, String description, LocalDate expenseDate, User paidBy, List<User> participants) {
        Expense expense = new Expense(label, amount, description, expenseDate, paidBy);
        expense.splitAmount(participants);
        expenseRepository.save(expense);
        for (ExpenseShare share : expense.getShares()) {
            expenseShareRepository.save(share);
        }
        return expense;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Optional<Expense> getExpenseById(UUID id) {
        return expenseRepository.findById(id);
    }

    public List<Expense> getExpensesByUser(UUID userId) {
        return expenseRepository.findByPaidBy(userId);
    }

    public List<Expense> getExpensesByMonth(int month, int year) {
        return expenseRepository.findByMonth(month, year);
    }

    public double getTotalDueByUser(UUID userId) {
        return expenseShareRepository.getTotalDueByUser(userId);
    }

    public void deleteExpense(UUID id) {
        expenseRepository.delete(id);
    }
}
