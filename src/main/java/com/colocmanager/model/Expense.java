package com.colocmanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Expense{
    private UUID id;
    private String label;
    private Double amount;
    private String description;
    private LocalDate expenseDate;
    private List<ExpenseShare> shares;
    private User paidBy;

    public Expense(String label, Double amount, String description, LocalDate expenseDate, User paidBy){
        this.id = UUID.randomUUID();
        this.label = label;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.paidBy = paidBy;
        this.shares = new ArrayList<>();

    }
}
