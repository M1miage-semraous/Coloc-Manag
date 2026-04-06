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

    private static final String TEAL = "#10B981";
    private final ExpenseController controller;

    public ExpenseView(Stage stage, User user) {
        this.controller = new ExpenseController(user);
        build(stage, user);
    }

    private void build(Stage stage, User user) {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F8FAFC;");

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
        scroll.setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("ColocManager — Dépenses");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane buildHeader(User user) {
        Circle deco1 = new Circle(70);
        deco1.setFill(Color.web("#FFFFFF", 0.06));
        deco1.setTranslateX(500);
        deco1.setTranslateY(-15);

        Circle deco2 = new Circle(45);
        deco2.setFill(Color.web("#FFFFFF", 0.05));
        deco2.setTranslateX(700);
        deco2.setTranslateY(10);

        Rectangle deco3 = new Rectangle(90, 90);
        deco3.setFill(Color.web("#FFFFFF", 0.04));
        deco3.setRotate(45);
        deco3.setTranslateX(650);
        deco3.setTranslateY(-25);

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 8; -fx-border-width: 1;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        Text title = new Text("💰  Gestion des dépenses");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setFill(Color.WHITE);

        double total = controller.getTotalExpenses();
        double myDue = controller.getMyDue();

        Text subtitle = new Text(String.format("Total colocation : %.2f €  •  Mon solde : %.2f €", total, myDue));
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#FFFFFF", 0.75));

        // Mini stats dans le header
        HBox miniStats = new HBox(20,
                miniStat("💳", "Total", String.format("%.2f €", total)),
                miniStat("👤", "Mon solde", String.format("%.2f €", myDue)),
                miniStat("📊", "Dépenses", String.valueOf(MainApp.expenseService.getAllExpenses().size()))
        );

        VBox headerContent = new VBox(8, btnRetour, title, subtitle, miniStats);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, deco3, headerContent);
        header.setStyle("-fx-background-color: linear-gradient(to right, #064E3B, #059669, #10B981);");
        header.setPadding(new Insets(28, 36, 28, 36));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);
        return header;
    }

    private HBox miniStat(String icon, String label, String value) {
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(20));

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 11px;");

        VBox box = new VBox(2, labelLbl, valueLbl);
        HBox stat = new HBox(10, iconLbl, box);
        stat.setAlignment(Pos.CENTER_LEFT);
        stat.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 10; -fx-padding: 10 16;");
        return stat;
    }

    private VBox buildCreateExpenseCard(User user) {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label cardTitle = new Label("➕  Ajouter une dépense");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        TextField tfLabel = new TextField();
        tfLabel.setPromptText("Libellé de la dépense");
        styleField(tfLabel);

        TextField tfMontant = new TextField();
        tfMontant.setPromptText("Montant (€)");
        styleField(tfMontant);

        Label lblResult = new Label();
        lblResult.setWrapText(true);
        lblResult.setMaxWidth(Double.MAX_VALUE);

        ListView<String> expenseList = new ListView<>();

        Button btnAjouter = new Button("✓  Ajouter la dépense");
        btnAjouter.setMaxWidth(Double.MAX_VALUE);
        btnAjouter.setStyle("-fx-background-color: linear-gradient(to right, #059669, #10B981); -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.3), 8, 0, 0, 3);");
        btnAjouter.setOnAction(e -> {
            controller.handleCreateExpense(tfLabel.getText().trim(), tfMontant.getText().trim(), lblResult, expenseList);
            if (lblResult.getText().startsWith("✓")) {
                lblResult.setStyle("-fx-text-fill: #065F46; -fx-background-color: #D1FAE5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px;");
                tfLabel.clear();
                tfMontant.clear();
            } else {
                lblResult.setStyle("-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px;");
            }
        });

        card.getChildren().addAll(cardTitle, new Separator(), tfLabel, tfMontant, btnAjouter, lblResult);
        return card;
    }

    private VBox buildSummaryCard(User user) {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label title = new Label("📊  Résumé financier");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        double total = controller.getTotalExpenses();
        double myDue = controller.getMyDue();
        int nbUsers = MainApp.userService.getAllUsers().size();

        VBox stats = new VBox(10,
                summaryRow("💳 Total des dépenses", String.format("%.2f €", total), "#6366F1"),
                summaryRow("👤 Mon solde restant",  String.format("%.2f €", myDue),  myDue > 0 ? "#EF4444" : "#10B981"),
                summaryRow("👥 Nombre de colocataires", String.valueOf(nbUsers), "#8B5CF6"),
                summaryRow("📈 Moyenne par personne", nbUsers > 0 ? String.format("%.2f €", total / nbUsers) : "0.00 €", "#F59E0B")
        );

        card.getChildren().addAll(title, new Separator(), stats);
        return card;
    }

    private HBox summaryRow(String label, String value, String color) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        HBox.setHgrow(lblLabel, Priority.ALWAYS);

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        HBox row = new HBox(lblLabel, lblValue);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8;");
        return row;
    }

    private VBox buildExpenseListCard(User user) {
        VBox card = new VBox(16);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label cardTitle = new Label("💳  Historique des dépenses");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox expenseListBox = new VBox(10);
        List<Expense> expenses = MainApp.expenseService.getAllExpenses();

        if (expenses.isEmpty()) {
            Label empty = new Label("Aucune dépense enregistrée.");
            empty.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px; -fx-padding: 20;");
            expenseListBox.getChildren().add(empty);
        } else {
            for (Expense expense : expenses) {
                expenseListBox.getChildren().add(buildExpenseRow(expense, user));
            }
        }

        card.getChildren().addAll(cardTitle, new Separator(), expenseListBox);
        return card;
    }

    private VBox buildExpenseRow(Expense expense, User user) {
        String paidBy = expense.getPaidBy() != null ? expense.getPaidBy().getFullName() : "-";

        Label labelLbl = new Label("💳 " + expense.getLabel());
        labelLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label amountLbl = new Label(String.format("%.2f €", expense.getAmount()));
        amountLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #059669;");

        Label paidByLbl = new Label("Payé par " + paidBy);
        paidByLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

        Label dateLbl = new Label("🗓 " + expense.getExpenseDate());
        dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");

        HBox topRow = new HBox(labelLbl);
        HBox.setHgrow(topRow, Priority.ALWAYS);
        topRow.getChildren().add(amountLbl);
        topRow.setAlignment(Pos.CENTER_LEFT);

        HBox metaRow = new HBox(12, paidByLbl, dateLbl);

        // Shares
        VBox sharesBox = new VBox(4);
        if (!expense.getShares().isEmpty()) {
            for (ExpenseShare share : expense.getShares()) {
                String name = share.getUser() != null ? share.getUser().getFullName() : "-";
                String paid = share.isPaid() ? "✅ Payé" : "⏳ En attente";
                String color = share.isPaid() ? "#059669" : "#D97706";
                Label shareLbl = new Label("   → " + name + " : " + String.format("%.2f €", share.getAmountDue()) + "  " + paid);
                shareLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + color + ";");
                sharesBox.getChildren().add(shareLbl);
            }
        }

        VBox row = new VBox(6, topRow, metaRow, sharesBox);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10; -fx-border-color: #E5E7EB; -fx-border-radius: 10; -fx-border-width: 1;");

        if (user.getRole() == Role.ADMIN) {
            Button btnSuppr = new Button("🗑 Supprimer");
            btnSuppr.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 5 10;");
            btnSuppr.setOnAction(e -> {
                MainApp.expenseService.deleteExpense(expense.getId());
                SceneManager.showExpenses(user);
            });
            row.getChildren().add(btnSuppr);
        }

        return row;
    }

    private VBox buildMySharesCard(User user) {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label title = new Label("💸  Mes parts à payer");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox sharesBox = new VBox(10);
        refreshSharesBox(sharesBox, user);

        card.getChildren().addAll(title, new Separator(), sharesBox);
        return card;
    }

    private void refreshSharesBox(VBox sharesBox, User user) {
        sharesBox.getChildren().clear();
        List<ExpenseShare> shares = MainApp.expenseService.getSharesForUser(user.getId());

        if (shares.isEmpty()) {
            Label empty = new Label("Aucune part à payer. Tout est à jour ! ✅");
            empty.setStyle("-fx-text-fill: #059669; -fx-font-size: 13px; -fx-padding: 12;");
            sharesBox.getChildren().add(empty);
            return;
        }

        for (ExpenseShare share : shares) {
            String status = share.isPaid() ? "✅ Payé" : "⏳ En attente";
            String color  = share.isPaid() ? "#059669" : "#D97706";
            String bg     = share.isPaid() ? "#D1FAE5" : "#FEF3C7";

            Label amountLbl = new Label(String.format("%.2f €", share.getAmountDue()));
            amountLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
            HBox.setHgrow(amountLbl, Priority.ALWAYS);

            Label statusLbl = new Label(status);
            statusLbl.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + color + "; -fx-background-radius: 20; -fx-padding: 4 12; -fx-font-size: 11px; -fx-font-weight: bold;");

            HBox row = new HBox(12, amountLbl, statusLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10; -fx-border-color: " + color + "30; -fx-border-radius: 10; -fx-border-width: 1;");

            if (!share.isPaid()) {
                Button btnPayer = new Button("Marquer comme payé ✓");
                btnPayer.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 7 14;");
                btnPayer.setOnAction(e -> {
                    MainApp.expenseService.markShareAsPaid(share.getId());
                    refreshSharesBox(sharesBox, user);
                });
                row.getChildren().add(btnPayer);
            }

            sharesBox.getChildren().add(row);
        }
    }

    private VBox buildRecapCard() {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label title = new Label("👥  Récapitulatif des paiements");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox recapBox = new VBox(10);
        List<Expense> expenses = MainApp.expenseService.getAllExpenses();

        if (expenses.isEmpty()) {
            recapBox.getChildren().add(new Label("Aucune dépense."));
        } else {
            for (Expense expense : expenses) {
                Label expLbl = new Label("💳 " + expense.getLabel() + " — " + String.format("%.2f €", expense.getAmount()));
                expLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
                recapBox.getChildren().add(expLbl);

                for (ExpenseShare share : expense.getShares()) {
                    String name = share.getUser() != null ? share.getUser().getFullName() : "-";
                    String paid = share.isPaid() ? "✅ Payé" : "⏳ En attente";
                    String color = share.isPaid() ? "#059669" : "#D97706";
                    Label shareLbl = new Label("   → " + name + " : " + String.format("%.2f €", share.getAmountDue()) + "  " + paid);
                    shareLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
                    recapBox.getChildren().add(shareLbl);
                }
                recapBox.getChildren().add(new Separator());
            }
        }

        card.getChildren().addAll(title, new Separator(), recapBox);
        return card;
    }

    private void styleField(TextField tf) {
        tf.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 14; -fx-font-size: 13px;");
        tf.setMaxWidth(Double.MAX_VALUE);
    }
}