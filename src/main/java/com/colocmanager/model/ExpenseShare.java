package com.colocmanager.model;

import java.util.UUID;

public class ExpenseShare {

    private UUID id;
    private Double amountDue;
    private boolean isPaid;
    private User user;

    public ExpenseShare(User user, Double amountDue) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.amountDue = amountDue;
        this.isPaid = false;
    }

    public void markAsPaid() {
        this.isPaid = true;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Double getAmountDue() { return amountDue; }
    public void setAmountDue(Double amountDue) { this.amountDue = amountDue; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { this.isPaid = paid; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "ExpenseShare{user=" + user.getFullName() + ", amountDue=" + amountDue + ", isPaid=" + isPaid + "}";
    }
}