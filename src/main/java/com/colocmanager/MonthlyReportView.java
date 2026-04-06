package com.colocmanager;

import com.colocmanager.controller.MonthlyReportController;
import com.colocmanager.model.MonthlyReport;
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

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlyReportView {

    public MonthlyReportView(Stage stage, User user) {
        MonthlyReportController controller = new MonthlyReportController(user);
        build(stage, user, controller);
    }

    private void build(Stage stage, User user, MonthlyReportController controller) {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F1F5F9;");

        StackPane header = buildHeader(user);

        ScrollPane scroll = new ScrollPane();
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(24, 36, 36, 36));
        mainContent.getChildren().add(buildSelectorCard(controller, mainContent, user));

        scroll.setContent(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F1F5F9; -fx-background: #F1F5F9;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("ColocManager — Rapport mensuel");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane buildHeader(User user) {
        Circle deco1 = new Circle(100);
        deco1.setFill(Color.web("#FFFFFF", 0.05));
        deco1.setTranslateX(500);
        deco1.setTranslateY(-20);
        deco1.setEffect(new GaussianBlur(20));

        Circle deco2 = new Circle(140);
        deco2.setFill(Color.web("#FFFFFF", 0.04));
        deco2.setTranslateX(750);
        deco2.setTranslateY(10);
        deco2.setEffect(new GaussianBlur(30));

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle(
                "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white;" +
                        "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;" +
                        "-fx-border-color: rgba(255,255,255,0.25); -fx-border-radius: 8;" +
                        "-fx-border-width: 1; -fx-font-size: 13px; -fx-font-weight: bold;"
        );
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        Text title = new Text("Rapport mensuel");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);

        Text subtitle = new Text("Analysez vos performances et dépenses mois par mois");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#FFFFFF", 0.7));

        VBox headerContent = new VBox(8, btnRetour, title, subtitle);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, headerContent);
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #0C4A6E, #0369A1, #0EA5E9);"
        );
        header.setPadding(new Insets(28, 40, 28, 40));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);
        return header;
    }

    private VBox buildSelectorCard(MonthlyReportController controller,
                                   VBox mainContent, User user) {
        VBox card = new VBox(16);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16;" +
                        "-fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3);" +
                        "-fx-padding: 24;"
        );

        Label title = new Label("Sélectionner la période");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        ComboBox<String> cbMois = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cbMois.getItems().add(Month.of(i).getDisplayName(TextStyle.FULL, Locale.FRENCH));
        }
        cbMois.getSelectionModel().select(controller.getCurrentMonth() - 1);
        cbMois.setPrefWidth(200);
        cbMois.setPrefHeight(44);
        cbMois.setStyle(
                "-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;" +
                        "-fx-font-size: 13px;"
        );

        ComboBox<Integer> cbAnnee = new ComboBox<>();
        int currentYear = controller.getCurrentYear();
        for (int y = currentYear - 2; y <= currentYear; y++) {
            cbAnnee.getItems().add(y);
        }
        cbAnnee.getSelectionModel().select(Integer.valueOf(currentYear));
        cbAnnee.setPrefWidth(120);
        cbAnnee.setPrefHeight(44);
        cbAnnee.setStyle(
                "-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;" +
                        "-fx-font-size: 13px;"
        );

        Button btnGenerer = new Button("Générer le rapport");
        btnGenerer.setPrefHeight(44);
        btnGenerer.setStyle(
                "-fx-background-color: linear-gradient(to right, #0369A1, #0EA5E9);" +
                        "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 0 24;" +
                        "-fx-effect: dropshadow(gaussian, rgba(14,165,233,0.3), 8, 0, 0, 3);"
        );

        Label moisLabel = new Label("Mois :");
        moisLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");

        Label anneeLabel = new Label("Année :");
        anneeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");

        HBox selRow = new HBox(12, moisLabel, cbMois, anneeLabel, cbAnnee, btnGenerer);
        selRow.setAlignment(Pos.CENTER_LEFT);

        VBox reportZone = new VBox(16);
        reportZone.setVisible(false);
        reportZone.setManaged(false);

        btnGenerer.setOnAction(e -> {
            int mois  = cbMois.getSelectionModel().getSelectedIndex() + 1;
            int annee = cbAnnee.getSelectionModel().getSelectedItem();
            MonthlyReport report = controller.generateReport(mois, annee);
            String nomMois = Month.of(mois).getDisplayName(TextStyle.FULL, Locale.FRENCH);

            reportZone.getChildren().clear();
            reportZone.setVisible(true);
            reportZone.setManaged(true);
            buildReportContent(reportZone, report, nomMois, annee, user);
        });

        card.getChildren().addAll(title, new Separator(), selRow, reportZone);
        return card;
    }

    private void buildReportContent(VBox zone, MonthlyReport report,
                                    String nomMois, int annee, User user) {
        // Titre rapport
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        StackPane titleIcon = new StackPane();
        titleIcon.setStyle(
                "-fx-background-color: #DBEAFE; -fx-background-radius: 10;" +
                        "-fx-min-width: 44; -fx-min-height: 44; -fx-max-width: 44; -fx-max-height: 44;"
        );
        Label titleIconLbl = new Label("≈");
        titleIconLbl.setStyle("-fx-font-size: 20px; -fx-text-fill: #1D4ED8; -fx-font-weight: bold;");
        titleIcon.getChildren().add(titleIconLbl);

        VBox titleInfo = new VBox(2,
                new Label("Rapport de " + nomMois + " " + annee) {{
                    setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");
                }},
                new Label(user.getFullName() + "  •  " +
                        (user.getRole().name().equals("ADMIN") ? "Administrateur" : "Colocataire")) {{
                    setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
                }}
        );
        titleRow.getChildren().addAll(titleIcon, titleInfo);

        // Section tâches
        Label taskSectionLbl = new Label("Tâches du mois");
        taskSectionLbl.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #6B7280;" +
                        "-fx-padding: 8 0 0 0;"
        );

        HBox taskStats = new HBox(16,
                reportStatCard("≡", "Assignées",
                        String.valueOf(report.getTotalTasksAssigned()), "#6366F1", "#EEF2FF"),
                reportStatCard("✓", "Validées",
                        String.valueOf(report.getTotalTasksValidated()), "#10B981", "#D1FAE5"),
                reportStatCard("✗", "Rejetées",
                        String.valueOf(report.getTotalTasksRejected()), "#EF4444", "#FEE2E2")
        );

        // Barre de progression tâches
        int total = report.getTotalTasksAssigned();
        int validated = report.getTotalTasksValidated();
        double taux = total > 0 ? (double) validated / total : 0;
        String tauxStr = String.format("%.0f%%", taux * 100);

        VBox progressCard = new VBox(10);
        progressCard.setStyle(
                "-fx-background-color: #F0F9FF; -fx-background-radius: 12;" +
                        "-fx-border-color: #BAE6FD; -fx-border-radius: 12; -fx-border-width: 1;" +
                        "-fx-padding: 16;"
        );

        Label progressTitle = new Label("Taux de validation : " + tauxStr);
        progressTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0369A1;");

        HBox progressBar = new HBox();
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-background-color: #E0F2FE; -fx-background-radius: 5;");
        progressBar.setPrefWidth(Double.MAX_VALUE);

        Region fill = new Region();
        fill.setPrefHeight(10);
        fill.setStyle(
                "-fx-background-color: linear-gradient(to right, #0369A1, #0EA5E9);" +
                        "-fx-background-radius: 5;"
        );
        fill.setPrefWidth(taux * 600);

        HBox barContainer = new HBox(fill);
        barContainer.setStyle("-fx-background-color: #E0F2FE; -fx-background-radius: 5;");
        barContainer.setPrefHeight(10);
        HBox.setHgrow(barContainer, Priority.ALWAYS);

        progressCard.getChildren().addAll(progressTitle, barContainer);

        // Section finances
        Label financeSectionLbl = new Label("Finances du mois");
        financeSectionLbl.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #6B7280;" +
                        "-fx-padding: 8 0 0 0;"
        );

        double soldeNet = report.getTotalPaid() - report.getTotalDue();
        String soldeColor = soldeNet >= 0 ? "#10B981" : "#EF4444";
        String soldeBg    = soldeNet >= 0 ? "#D1FAE5" : "#FEE2E2";

        HBox financeStats = new HBox(16,
                reportStatCard("€", "Total payé",
                        String.format("%.2f €", report.getTotalPaid()), "#059669", "#D1FAE5"),
                reportStatCard("↑", "Total dû",
                        String.format("%.2f €", report.getTotalDue()), "#D97706", "#FEF3C7"),
                reportStatCard("=", "Solde net",
                        String.format("%.2f €", soldeNet), soldeColor, soldeBg)
        );

        // Analyse
        VBox analysisCard = new VBox(12);
        analysisCard.setStyle(
                "-fx-background-color: " + (soldeNet >= 0 ? "#F0FDF4" : "#FFF7ED") + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + (soldeNet >= 0 ? "#BBF7D0" : "#FED7AA") + ";" +
                        "-fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 16;"
        );

        Label analysisTitle = new Label(
                soldeNet >= 0 ? "✓  Bilan positif ce mois" : "!  Bilan à surveiller"
        );
        analysisTitle.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold;" +
                        "-fx-text-fill: " + (soldeNet >= 0 ? "#065F46" : "#92400E") + ";"
        );

        Label analysis1 = new Label("• Taux de validation : " + tauxStr);
        analysis1.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        Label analysis2 = new Label("• " + (soldeNet >= 0
                ? "Vous avez avancé " + String.format("%.2f €", soldeNet) + " pour la colocation."
                : "Vous devez encore " + String.format("%.2f €", Math.abs(soldeNet)) + " ce mois-ci."));
        analysis2.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        Label analysis3 = new Label("• " + report.getTotalTasksAssigned() +
                " tâche(s) assignée(s), " + report.getTotalTasksValidated() + " validée(s).");
        analysis3.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

        analysisCard.getChildren().addAll(analysisTitle, analysis1, analysis2, analysis3);

        zone.getChildren().addAll(
                new Separator(),
                titleRow,
                taskSectionLbl,
                taskStats,
                progressCard,
                financeSectionLbl,
                financeStats,
                analysisCard
        );
    }

    private VBox reportStatCard(String icon, String title, String value,
                                String color, String bg) {
        StackPane iconBox = new StackPane();
        iconBox.setStyle(
                "-fx-background-color: " + color + "20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-min-width: 44; -fx-min-height: 44;" +
                        "-fx-max-width: 44; -fx-max-height: 44;"
        );
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        iconBox.getChildren().add(iconLbl);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Rectangle bar = new Rectangle(36, 3);
        bar.setFill(Color.web(color));
        bar.setArcWidth(3);
        bar.setArcHeight(3);

        VBox card = new VBox(6, iconBox, titleLbl, valueLbl, bar);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14;" +
                        "-fx-border-radius: 14; -fx-border-color: " + color + "30; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);" +
                        "-fx-padding: 18 22;"
        );
        card.setPrefWidth(180);
        HBox.setHgrow(card, Priority.ALWAYS);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: " + bg + "; -fx-background-radius: 14;" +
                        "-fx-border-radius: 14; -fx-border-color: " + color + "; -fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, " + color + "40, 12, 0, 0, 4);" +
                        "-fx-padding: 18 22; -fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14;" +
                        "-fx-border-radius: 14; -fx-border-color: " + color + "30; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 3);" +
                        "-fx-padding: 18 22;"
        ));

        return card;
    }
}