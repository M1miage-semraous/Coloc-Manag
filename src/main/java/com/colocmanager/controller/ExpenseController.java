package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.model.Expense;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.List;

public class ExpenseController {

    private final User currentUser;

    public ExpenseController(User currentUser) {
        this.currentUser = currentUser;
    }

    public void handleCreateExpense(String label, String montantStr,
                                    Label lblResult, ListView<String> expenseList) {
        if (label.isBlank()) {
            lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblResult.setText("✗ Le libellé est obligatoire.");
            return;
        }
        try {
            double montant = Double.parseDouble(montantStr.replace(",", "."));
            if (montant <= 0) {
                lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
                lblResult.setText("✗ Le montant doit être positif.");
                return;
            }
            List<User> users = MainApp.userService.getAllUsers();
            MainApp.expenseService.createExpense(label, montant, "", LocalDate.now(), currentUser, users);
            lblResult.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
            lblResult.setText("✓ Dépense ajoutée : " + label);
            refreshExpenseList(expenseList);
        } catch (NumberFormatException e) {
            lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblResult.setText("✗ Montant invalide.");
        }
    }

    public void handleDeleteExpense(int index, ListView<String> expenseList, Label lblResult) {
        List<Expense> expenses = MainApp.expenseService.getAllExpenses();
        if (index < 0 || index >= expenses.size()) {
            lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblResult.setText("✗ Sélectionnez une dépense.");
            return;
        }
        Expense expense = expenses.get(index);
        MainApp.expenseService.deleteExpense(expense.getId());
        lblResult.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
        lblResult.setText("✓ Dépense supprimée.");
        refreshExpenseList(expenseList);
    }

    public void refreshExpenseList(ListView<String> listView) {
        listView.getItems().clear();
        for (Expense ex : MainApp.expenseService.getAllExpenses()) {
            String paidBy = ex.getPaidBy() != null ? ex.getPaidBy().getFullName() : "-";
            listView.getItems().add(
                    "💳 " + ex.getLabel()
                            + "  |  " + String.format("%.2f", ex.getAmount()) + "€"
                            + "  |  Payé par: " + paidBy
                            + "  |  " + ex.getExpenseDate()
            );
            for (ExpenseShare share : ex.getShares()) {
                String userName = share.getUser() != null ? share.getUser().getFullName() : "-";
                listView.getItems().add(
                        "     → " + userName + " doit " + String.format("%.2f", share.getAmountDue()) + "€"
                );
            }
        }
    }

    public double getMyDue() {
        return MainApp.expenseService.getTotalDueByUser(currentUser.getId());
    }

    public double getTotalExpenses() {
        return MainApp.expenseService.getAllExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
