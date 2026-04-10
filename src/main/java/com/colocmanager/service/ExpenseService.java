package com.colocmanager.service;

import com.colocmanager.enums.NotificationType;
import com.colocmanager.model.Expense;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;
import com.colocmanager.repository.ExpenseRepository;
import com.colocmanager.repository.ExpenseShareRepository;
import com.colocmanager.repository.NotificationRepository;
import com.colocmanager.model.Notification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final NotificationRepository notificationRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          ExpenseShareRepository expenseShareRepository,
                          NotificationRepository notificationRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseShareRepository = expenseShareRepository;
        this.notificationRepository = notificationRepository;
    }

    public ExpenseRepository getExpenseRepository() {
        return expenseRepository;
    }

    public Expense createExpense(String label, Double amount, String description,
                                 LocalDate expenseDate, User paidBy, List<User> participants) {
        Expense expense = new Expense(label, amount, description, expenseDate, paidBy);
        expense.splitAmount(participants);
        expenseRepository.save(expense);

        for (ExpenseShare share : expense.getShares()) {
            expenseShareRepository.save(share, expense.getId());

            // Notification pour chaque participant
            String msg = String.format(
                    "Nouvelle dépense '%s' de %.2f € — Votre part : %.2f €. Veuillez payer votre part.",
                    label, amount, share.getAmountDue()
            );
            Notification notif = new Notification(
                    "Nouvelle dépense",
                    msg,
                    NotificationType.TASK_ASSIGNED,
                    share.getUser()
            );
            notificationRepository.save(notif);
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
        expenseShareRepository.deleteByExpenseId(id);
        expenseRepository.delete(id);
    }

    public void markShareAsPaid(UUID shareId) {
        expenseShareRepository.markAsPaid(shareId);
    }

    public List<ExpenseShare> getSharesForUser(UUID userId) {
        return expenseShareRepository.findByUserId(userId);
    }
}