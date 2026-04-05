package com.colocmanager;

import com.colocmanager.controller.MonthlyReportController;
import com.colocmanager.model.MonthlyReport;
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

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlyReportView {

    private static final String NAVY  = "#1A2B4A";
    private static final String TEAL  = "#0D9488";
    private static final String GREY  = "#F8FAFC";
    private static final String SLATE = "#64748B";

    public MonthlyReportView(Stage stage, User user) {
        MonthlyReportController controller = new MonthlyReportController(user);
        build(stage, user, controller);
    }

    private void build(Stage stage, User user, MonthlyReportController controller) {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));
        pane.setStyle("-fx-background-color: " + GREY + ";");

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: transparent; -fx-text-fill: " + NAVY + "; -fx-font-size: 13; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        Text titre = new Text("Rapport mensuel");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        // Sélection mois/année
        ComboBox<String> cbMois = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMois.getItems().add(Month.of(i).getDisplayName(TextStyle.FULL, Locale.FRENCH));
        }
        cbMois.getSelectionModel().select(controller.getCurrentMonth() - 1);

        ComboBox<Integer> cbAnnee = new ComboBox<>();
        int currentYear = controller.getCurrentYear();
        for (int y = currentYear - 2; y <= currentYear; y++) {
            cbAnnee.getItems().add(y);
        }
        cbAnnee.getSelectionModel().select(Integer.valueOf(currentYear));

        Button btnGenerer = new Button("📊 Générer le rapport");
        btnGenerer.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-size: 13; -fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 16;");

        HBox selectionRow = new HBox(12, new Label("Mois :"), cbMois, new Label("Année :"), cbAnnee, btnGenerer);
        selectionRow.setAlignment(Pos.CENTER_LEFT);

        // Zone rapport
        VBox reportZone = new VBox(12);
        reportZone.setPadding(new Insets(20));
        reportZone.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E2E8F0;");
        reportZone.setVisible(false);

        btnGenerer.setOnAction(e -> {
            int mois = cbMois.getSelectionModel().getSelectedIndex() + 1;
            int annee = cbAnnee.getSelectionModel().getSelectedItem();

            MonthlyReport report = controller.generateReport(mois, annee);

            String nomMois = Month.of(mois).getDisplayName(TextStyle.FULL, Locale.FRENCH);

            reportZone.getChildren().clear();

            Text reportTitre = new Text("Rapport de " + nomMois + " " + annee + " — " + user.getFullName());
            reportTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            reportTitre.setFill(Color.web(NAVY));

            Separator sep = new Separator();

            HBox statsRow = new HBox(16,
                    statCard("Tâches assignées",  String.valueOf(report.getTotalTasksAssigned())),
                    statCard("Tâches validées",   String.valueOf(report.getTotalTasksValidated())),
                    statCard("Tâches rejetées",   String.valueOf(report.getTotalTasksRejected()))
            );

            HBox financeRow = new HBox(16,
                    statCard("Total payé",  String.format("%.2f €", report.getTotalPaid())),
                    statCard("Total dû",    String.format("%.2f €", report.getTotalDue())),
                    statCard("Solde net",   String.format("%.2f €", report.getTotalPaid() - report.getTotalDue()))
            );

            reportZone.getChildren().addAll(reportTitre, sep, statsRow, financeRow);
            reportZone.setVisible(true);
        });

        pane.getChildren().addAll(btnRetour, titre, selectionRow, reportZone);

        Scene scene = new Scene(pane, 1100, 700);
        stage.setTitle("ColocManager - Rapport mensuel");
        stage.setScene(scene);
        stage.show();
    }

    private VBox statCard(String title, String value) {
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13px;");
        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: " + NAVY + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        VBox card = new VBox(8, lblTitle, lblValue);
        card.setPadding(new Insets(16));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #E2E8F0;");
        return card;
    }
}