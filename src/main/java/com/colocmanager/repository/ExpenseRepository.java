package com.colocmanager.repository;

import com.colocmanager.model.Expense;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExpenseRepository {

    private List<Expense> expenses = new ArrayList<>();

    public void save(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> findAll() {
        return new ArrayList<>(expenses);
    }

    public Optional<Expense> findById(UUID id) {
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public List<Expense> findByPaidBy(UUID userId) {
        return expenses.stream()
                .filter(e -> e.getPaidBy().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Expense> findByMonth(int month, int year) {
        return expenses.stream()
                .filter(e -> e.getExpenseDate().getMonthValue() == month
                        && e.getExpenseDate().getYear() == year)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        expenses.removeIf(e -> e.getId().equals(id));
    }

    public int count() {
        return expenses.size();
    }
}