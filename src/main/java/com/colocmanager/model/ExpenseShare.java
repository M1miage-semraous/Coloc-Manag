package com.colocmanager.model;

import java.util.UUID;

public class ExpenseShare {


    private UUID id;
    private Double amountDue;
    private User user;


    public ExpenseShare(User user, Double amountDue) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.amountDue = amountDue;
    }


    public UUID getId() { return id; }

    public Double getAmountDue() { return amountDue; }
    public void setAmountDue(Double amountDue) { this.amountDue = amountDue; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "ExpenseShare{user=" + user.getFullName() + ", amountDue=" + amountDue + "}";
    }
}












