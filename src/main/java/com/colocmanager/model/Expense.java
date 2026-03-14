package com.colocmanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Expense {


    private UUID id;
    private String label;
    private Double amount;
    private String description;
    private LocalDate expenseDate;
    private List<ExpenseShare> shares;
    private User paidBy;


    public Expense(String label, Double amount, String description, LocalDate expenseDate, User paidBy) {
        this.id = UUID.randomUUID();
        this.label = label;
        this.amount = amount;
        this.description = description;
        this.expenseDate = expenseDate;
        this.paidBy = paidBy;
        this.shares = new ArrayList<>();
    }


    public void splitAmount(List<User> participants) {
        this.shares.clear();

        if (participants == null || participants.isEmpty()) return;

        double share = this.amount / participants.size(); // calcul de la part égale pour chacun

        // pour chaque participant on crée un ExpenseShare avec son montant dû
        for (User user : participants) {
            this.shares.add(new ExpenseShare(user, share));
        }
    }


    public UUID getId() { return id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public List<ExpenseShare> getShares() { return shares; }
    public void setShares(List<ExpenseShare> shares) { this.shares = shares; }

    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", label='" + label + "', amount=" + amount +
                ", paidBy=" + paidBy.getFullName() + "}";
    }
}
