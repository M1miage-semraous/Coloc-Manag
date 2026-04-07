package com.colocmanager;

import com.colocmanager.controller.ExpenseController;
import com.colocmanager.enums.Role;
import com.colocmanager.model.Expense;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class ExpenseView {

    private final ExpenseController controller;

    public ExpenseView(Stage stage, User user) {
        this.controller = new ExpenseController(user);
        build(stage, user);
    }

    private void build(Stage stage, User user) {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F1F5F9;");

        StackPane header = buildHeader(user);

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));

        VBox leftCol = new VBox(16);
        leftCol.setPrefWidth(320);

        if (user.getRole() == Role.ADMIN) {
            leftCol.getChildren().add(buildCreateExpenseCard(user));
        }
        leftCol.getChildren().add(buildSummaryCard(user));

        VBox rightCol = new VBox(16);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        rightCol.getChildren().add(buildExpenseListCard(user));

        if (user.getRole() != Role.ADMIN) {
            rightCol.getChildren().add(buildMySharesCard(user));
        } else {
            rightCol.getChildren().add(buildRecapCard());
        }

        mainContent.getChildren().addAll(leftCol, rightCol);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F1F5F9; -fx-background: #F1F5F9;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("ColocManager — Dépenses");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane buildHeader(User user) {
        Circle deco1 = new Circle(100);
        deco1.setFill(Color.web("#FFFFFF", 0.06));
        deco1.setTranslateX(500);
        deco1.setTranslateY(-15);
        deco1.setEffect(new GaussianBlur(20));

        Circle deco2 = new Circle(140);
        deco2.setFill(Color.web("#FFFFFF", 0.04));
        deco2.setTranslateX(750);
        deco2.setTranslateY(10);
        deco2.setEffect(new GaussianBlur(30));

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white;" +
                        "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;" +
                        "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 8;" +
                        "-fx-border-width: 1; -fx-font-size: 13px; -fx-font-weight: bold;"
        );
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        Text title = new Text("Gestion des dépenses");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);

        double total = controller.getTotalExpenses();
        double myDue = controller.getMyDue();
        int nbExp = MainApp.expenseService.getAllExpenses().size();

        Text subtitle = new Text(
                String.format("Total colocation : %.2f €  •  Mon solde : %.2f €", total, myDue)
        );
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#FFFFFF", 0.75));

        // Mini stats
        HBox miniStats = new HBox(16,
                miniStat("Total", String.format("%.2f €", total)),
                miniStat("Mon solde", String.format("%.2f €", myDue)),
                miniStat("Dépenses", String.valueOf(nbExp))
        );

        VBox headerContent = new VBox(8, btnRetour, title, subtitle, miniStats);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, headerContent);
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #064E3B, #059669, #10B981);"
        );
        header.setPadding(new Insets(28, 40, 28, 40));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);
        return header;
    }

    private HBox miniStat(String label, String value) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 11px;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox box = new VBox(2, lblLabel, lblValue);
        box.setAlignment(Pos.CENTER_LEFT);

        HBox stat = new HBox(box);
        stat.setStyle(
                "-fx-background-color: rgba(255,255,255,0.12);" +
                        "-fx-background-radius: 10; -fx-padding: 10 16;"
        );
        return stat;
    }

    private VBox buildCreateExpenseCard(User user) {
        VBox card = buildCard("Ajouter une dépense");

        TextField tfLabel = new TextField();
        tfLabel.setPromptText("Libellé de la dépense");
        styleField(tfLabel);

        TextField tfMontant = new TextField();
        tfMontant.setPromptText("Montant (€)");
        styleField(tfMontant);

        Label lblResult = new Label();
        lblResult.setWrapText(true);
        lblResult.setMaxWidth(Double.MAX_VALUE);

        ListView<String> dummy = new ListView<>();

        Button btnAjouter = new Button("✓  Ajouter la dépense");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setStyle(
                "-fx-background-color: linear-gradient(to right, #059669, #10B981);" +
                        "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.3), 8, 0, 0, 3);"
        );
        btnAjouter.setOnAction(e -> {
            controller.handleCreateExpense(tfLabel.getText().trim(), tfMontant.getText().trim(), lblResult, dummy);
            if (lblResult.getText().startsWith("✓")) {
                lblResult.setStyle(
                        "-fx-text-fill: #065F46; -fx-background-color: #D1FAE5;" +
                                "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
                );
                tfLabel.clear();
                tfMontant.clear();
                SceneManager.showExpenses(user);
            } else {
                lblResult.setStyle(
                        "-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2;" +
                                "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
                );
            }
        });

        card.getChildren().addAll(tfLabel, tfMontant, btnAjouter, lblResult);
        return card;
    }

    private VBox buildSummaryCard(User user) {
        VBox card = buildCard("Résumé financier");

        double total = controller.getTotalExpenses();
        double myDue = controller.getMyDue();
        int nbUsers = MainApp.userService.getAllUsers().size();
        double avg = nbUsers > 0 ? total / nbUsers : 0;

        card.getChildren().addAll(
                financeRow("Total des dépenses",   String.format("%.2f €", total), "#6366F1"),
                financeRow("Mon solde restant",     String.format("%.2f €", myDue), myDue > 0 ? "#EF4444" : "#10B981"),
                financeRow("Nombre de colocataires", String.valueOf(nbUsers), "#8B5CF6"),
                financeRow("Moyenne par personne",  String.format("%.2f €", avg), "#F59E0B")
        );
        return card;
    }

    private HBox financeRow(String label, String value, String color) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        lblLabel.setMinWidth(160);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblValue = new Label(value);
        lblValue.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";"
        );

        HBox row = new HBox(lblLabel, spacer, lblValue);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10;");
        return row;
    }

    private VBox buildExpenseListCard(User user) {
        VBox card = buildCard("Historique des dépenses");

        List<Expense> expenses = MainApp.expenseService.getAllExpenses();

        if (expenses.isEmpty()) {
            Label empty = new Label("Aucune dépense enregistrée.");
            empty.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px; -fx-padding: 20;");
            card.getChildren().add(empty);
        } else {
            for (Expense expense : expenses) {
                card.getChildren().add(buildExpenseRow(expense, user));
            }
        }

        return card;
    }

    private VBox buildExpenseRow(Expense expense, User user) {
        String paidBy = expense.getPaidBy() != null ? expense.getPaidBy().getFullName() : "-";

        // Header ligne
        Label labelLbl = new Label(expense.getLabel());
        labelLbl.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;"
        );
        HBox.setHgrow(labelLbl, Priority.ALWAYS);

        Label amountLbl = new Label(String.format("%.2f €", expense.getAmount()));
        amountLbl.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #059669;" +
                        "-fx-background-color: #D1FAE5; -fx-background-radius: 8; -fx-padding: 3 10;"
        );

        HBox topRow = new HBox(12, labelLbl, amountLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Meta
        Label paidByLbl = new Label("Payé par  " + paidBy);
        paidByLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

        Label dateLbl = new Label(expense.getExpenseDate() != null ? expense.getExpenseDate().toString() : "");
        dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");

        HBox metaRow = new HBox(16, paidByLbl, dateLbl);

        // Shares
        VBox sharesBox = new VBox(4);
        if (!expense.getShares().isEmpty()) {
            for (ExpenseShare share : expense.getShares()) {
                String name = share.getUser() != null ? share.getUser().getFullName() : "-";
                boolean paid = share.isPaid();
                String status = paid ? "✓ Payé" : "En attente";
                String color  = paid ? "#059669" : "#D97706";
                String bg     = paid ? "#D1FAE5" : "#FEF3C7";

                Label nameLbl = new Label("→  " + name);
                nameLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #374151;");
                HBox.setHgrow(nameLbl, Priority.ALWAYS);

                Label amtLbl = new Label(String.format("%.2f €", share.getAmountDue()));
                amtLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

                Label statusLbl = new Label(status);
                statusLbl.setStyle(
                        "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + color + ";" +
                                "-fx-background-color: " + bg + "; -fx-background-radius: 20; -fx-padding: 2 8;"
                );

                HBox shareRow = new HBox(8, nameLbl, amtLbl, statusLbl);
                shareRow.setAlignment(Pos.CENTER_LEFT);
                shareRow.setPadding(new Insets(4, 8, 4, 8));
                shareRow.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 6;");
                sharesBox.getChildren().add(shareRow);
            }
        }

        VBox row = new VBox(8, topRow, metaRow, sharesBox);
        row.setPadding(new Insets(16, 18, 16, 18));
        row.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12;" +
                        "-fx-border-color: #E5E7EB; -fx-border-radius: 12; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 6, 0, 0, 2);"
        );

        if (user.getRole() == Role.ADMIN) {
            Button btnSuppr = new Button("Supprimer");
            btnSuppr.setStyle(
                    "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6;" +
                            "-fx-cursor: hand; -fx-padding: 5 12;"
            );
            btnSuppr.setOnAction(e -> {
                MainApp.expenseService.deleteExpense(expense.getId());
                SceneManager.showExpenses(user);
            });
            row.getChildren().add(btnSuppr);
        }

        return row;
    }

    private VBox buildMySharesCard(User user) {
        VBox card = buildCard("Mes parts à payer");

        List<ExpenseShare> shares = MainApp.expenseService.getSharesForUser(user.getId());

        if (shares.isEmpty()) {
            Label empty = new Label("Aucune part à payer. Tout est à jour ! ✓");
            empty.setStyle("-fx-text-fill: #059669; -fx-font-size: 13px; -fx-padding: 12;");
            card.getChildren().add(empty);
            return card;
        }

        VBox sharesBox = new VBox(10);
        for (ExpenseShare share : shares) {
            boolean paid = share.isPaid();
            String color = paid ? "#059669" : "#D97706";
            String bg    = paid ? "#D1FAE5" : "#FEF3C7";
            String status = paid ? "✓ Payé" : "En attente";

            Label amtLbl = new Label(String.format("%.2f €", share.getAmountDue()));
            amtLbl.setStyle(
                    "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";"
            );
            HBox.setHgrow(amtLbl, Priority.ALWAYS);

            Label statusLbl = new Label(status);
            statusLbl.setStyle(
                    "-fx-background-color: " + bg + "; -fx-text-fill: " + color + ";" +
                            "-fx-background-radius: 20; -fx-padding: 4 12;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold;"
            );

            HBox row = new HBox(12, amtLbl, statusLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle(
                    "-fx-background-color: #F9FAFB; -fx-background-radius: 10;" +
                            "-fx-border-color: " + color + "30; -fx-border-radius: 10; -fx-border-width: 1;"
            );

            if (!paid) {
                Button btnPayer = new Button("Marquer comme payé ✓");
                btnPayer.setStyle(
                        "-fx-background-color: #059669; -fx-text-fill: white;" +
                                "-fx-font-size: 11px; -fx-font-weight: bold;" +
                                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 7 14;"
                );
                btnPayer.setOnAction(e -> {
                    MainApp.expenseService.markShareAsPaid(share.getId());
                    SceneManager.showExpenses(user);
                });
                row.getChildren().add(btnPayer);
            }

            sharesBox.getChildren().add(row);
        }

        card.getChildren().add(sharesBox);
        return card;
    }

    private VBox buildRecapCard() {
        VBox card = buildCard("Récapitulatif des paiements");

        List<Expense> expenses = MainApp.expenseService.getAllExpenses();

        if (expenses.isEmpty()) {
            card.getChildren().add(new Label("Aucune dépense."));
            return card;
        }

        for (Expense expense : expenses) {
            Label expLbl = new Label(expense.getLabel() + "  —  " +
                    String.format("%.2f €", expense.getAmount()));
            expLbl.setStyle(
                    "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;" +
                            "-fx-padding: 4 0 4 0;"
            );
            card.getChildren().add(expLbl);

            for (ExpenseShare share : expense.getShares()) {
                String name = share.getUser() != null ? share.getUser().getFullName() : "-";
                boolean paid = share.isPaid();
                String color = paid ? "#059669" : "#D97706";
                String status = paid ? "✓ Payé" : "En attente";

                Label nameLbl = new Label("→  " + name);
                nameLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
                HBox.setHgrow(nameLbl, Priority.ALWAYS);

                Label amtLbl = new Label(String.format("%.2f €", share.getAmountDue()));
                amtLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

                Label statusLbl = new Label(status);
                statusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");

                HBox shareRow = new HBox(10, nameLbl, amtLbl, statusLbl);
                shareRow.setAlignment(Pos.CENTER_LEFT);
                shareRow.setPadding(new Insets(6, 12, 6, 12));
                shareRow.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8;");
                card.getChildren().add(shareRow);
            }
            card.getChildren().add(new Separator());
        }

        return card;
    }

    private VBox buildCard(String title) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;"
        );
        Separator sep = new Separator();

        VBox card = new VBox(12, titleLbl, sep);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16;" +
                        "-fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3);" +
                        "-fx-padding: 20;"
        );
        return card;
    }

    private void styleField(TextField tf) {
        tf.setStyle(
                "-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 10 14; -fx-font-size: 13px;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
    }
}