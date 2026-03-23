package com.colocmanager.repository;

import com.colocmanager.model.ExpenseShare;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExpenseShareRepository {

    private List<ExpenseShare> expenseShares = new ArrayList<>();

    public void save(ExpenseShare expenseShare) {
        expenseShares.add(expenseShare);
    }

    public List<ExpenseShare> findAll() {
        return new ArrayList<>(expenseShares);
    }

    public Optional<ExpenseShare> findById(UUID id) {
        return expenseShares.stream()
                .filter(es -> es.getId().equals(id))
                .findFirst();
    }

    public List<ExpenseShare> findByUserId(UUID userId) {
        return expenseShares.stream()
                .filter(es -> es.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public double getTotalDueByUser(UUID userId) {
        return expenseShares.stream()
                .filter(es -> es.getUser().getId().equals(userId))
                .mapToDouble(ExpenseShare::getAmountDue)
                .sum();
    }

    public void delete(UUID id) {
        expenseShares.removeIf(es -> es.getId().equals(id));
    }

    public int count() {
        return expenseShares.size();
    }
}
