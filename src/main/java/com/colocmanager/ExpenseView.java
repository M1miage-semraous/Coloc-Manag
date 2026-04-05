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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class ExpenseView {

    private static final String NAVY = "#1A2B4A";
    private static final String TEAL = "#0D9488";
    private static final String GREY = "#F8FAFC";

    private final ExpenseController controller;

    public ExpenseView(Stage stage, User user) {
        this.controller = new ExpenseController(user);
        build(stage, user);
    }

    private void build(Stage stage, User user) {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));
        pane.setStyle("-fx-background-color: " + GREY + ";");

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: transparent; -fx-text-fill: " + NAVY + "; -fx-font-size: 13; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        Text titre = new Text("Gestion des dépenses");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        HBox cards = new HBox(16,
                statCard("Total dépenses", String.format("%.2f €", controller.getTotalExpenses())),
                statCard("Mon solde", String.format("%.2f €", controller.getMyDue()))
        );

        Label lblResult = new Label();

        ListView<String> expenseList = new ListView<>();
        controller.refreshExpenseList(expenseList);
        VBox.setVgrow(expenseList, Priority.ALWAYS);

        pane.getChildren().addAll(btnRetour, titre, cards);

        // Formulaire admin uniquement
        if (user.getRole() == Role.ADMIN) {
            TextField tfLabel   = new TextField(); tfLabel.setPromptText("Libellé");       styleField(tfLabel);
            TextField tfMontant = new TextField(); tfMontant.setPromptText("Montant (€)"); styleField(tfMontant);

            Button btnAjouter = actionBtn("+ Ajouter");
            btnAjouter.setOnAction(e -> {
                controller.handleCreateExpense(
                        tfLabel.getText().trim(),
                        tfMontant.getText().trim(),
                        lblResult,
                        expenseList
                );
                tfLabel.clear();
                tfMontant.clear();
            });

            Button btnSupprimer = new Button("🗑 Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
            btnSupprimer.setOnAction(e ->
                    controller.handleDeleteExpense(
                            expenseList.getSelectionModel().getSelectedIndex(),
                            expenseList,
                            lblResult
                    )
            );

            HBox formRow = new HBox(10, tfLabel, tfMontant, btnAjouter, btnSupprimer);
            formRow.setAlignment(Pos.CENTER_LEFT);
            pane.getChildren().addAll(formRow, lblResult);
        }

        pane.getChildren().add(expenseList);

        // Mes parts à payer — visible pour TOUS
        Text titreShares = new Text("Mes parts à payer");
        titreShares.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titreShares.setFill(Color.web(NAVY));

        VBox sharesBox = new VBox(8);
        sharesBox.setPadding(new Insets(12));
        sharesBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E2E8F0;");
        refreshSharesBox(sharesBox, user);

        pane.getChildren().addAll(titreShares, sharesBox);

        // Récapitulatif des paiements — visible pour ADMIN uniquement
        if (user.getRole() == Role.ADMIN) {
            Text titreRecap = new Text("Récapitulatif des paiements");
            titreRecap.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            titreRecap.setFill(Color.web(NAVY));

            VBox recapBox = new VBox(8);
            recapBox.setPadding(new Insets(12));
            recapBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E2E8F0;");
            refreshRecapBox(recapBox);

            pane.getChildren().addAll(titreRecap, recapBox);
        }

        Scene scene = new Scene(pane, 1100, 700);
        stage.setTitle("ColocManager - Dépenses");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshSharesBox(VBox sharesBox, User user) {
        sharesBox.getChildren().clear();
        List<ExpenseShare> shares = MainApp.expenseService.getSharesForUser(user.getId());

        if (shares.isEmpty()) {
            sharesBox.getChildren().add(new Label("Aucune part à payer."));
            return;
        }

        for (ExpenseShare share : shares) {
            String status = share.isPaid() ? "✅ Payé" : "⏳ En attente";
            String color  = share.isPaid() ? "#0D9488" : "#DC2626";

            Label lbl = new Label(String.format("%.2f €", share.getAmountDue()) + "  " + status);
            lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + color + ";");

            HBox row = new HBox(12, lbl);
            row.setAlignment(Pos.CENTER_LEFT);

            if (!share.isPaid()) {
                Button btnPayer = actionBtn("Marquer comme payé");
                btnPayer.setOnAction(e -> {
                    MainApp.expenseService.markShareAsPaid(share.getId());
                    refreshSharesBox(sharesBox, user);
                });
                row.getChildren().add(btnPayer);
            }

            sharesBox.getChildren().add(row);
        }
    }

    private void refreshRecapBox(VBox recapBox) {
        recapBox.getChildren().clear();
        List<Expense> expenses = MainApp.expenseService.getAllExpenses();

        if (expenses.isEmpty()) {
            recapBox.getChildren().add(new Label("Aucune dépense enregistrée."));
            return;
        }

        for (Expense expense : expenses) {
            Label expenseLabel = new Label("💳 " + expense.getLabel() +
                    " — " + String.format("%.2f €", expense.getAmount()));
            expenseLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");
            recapBox.getChildren().add(expenseLabel);

            for (ExpenseShare share : expense.getShares()) {
                String userName = share.getUser() != null ? share.getUser().getFullName() : "-";
                String status   = share.isPaid() ? "✅ Payé" : "⏳ En attente";
                String color    = share.isPaid() ? "#0D9488" : "#DC2626";

                Label shareLbl = new Label("   → " + userName +
                        " : " + String.format("%.2f €", share.getAmountDue()) +
                        "  " + status);
                shareLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
                recapBox.getChildren().add(shareLbl);
            }

            recapBox.getChildren().add(new Separator());
        }
    }

    private VBox statCard(String title, String value) {
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: " + NAVY + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        VBox card = new VBox(8, lblTitle, lblValue);
        card.setPadding(new Insets(16));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E2E8F0;");
        return card;
    }

    private Button actionBtn(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    private void styleField(TextField tf) {
        tf.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-font-size: 12; -fx-pref-height: 34;");
    }
}