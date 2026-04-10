package com.colocmanager;

import com.colocmanager.controller.NotificationController;
import com.colocmanager.model.Notification;
import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NotificationView {

    private final List<Notification> selectedNotifs = new ArrayList<>();
    private VBox notifListBox;
    private User currentUser;

    public NotificationView(Stage stage, User user) {
        this.currentUser = user;
        NotificationController controller = new NotificationController(user);
        build(stage, user, controller);
    }

    private void build(Stage stage, User user, NotificationController controller) {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F1F5F9;");

        StackPane header = buildHeader(user, controller);

        HBox content = new HBox(20);
        content.setPadding(new Insets(24, 36, 36, 36));
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox leftCol = new VBox(16);
        leftCol.setPrefWidth(280);
        leftCol.getChildren().addAll(
                buildStatsCard(controller),
                buildActionsCard(user, controller)
        );

        VBox rightCol = new VBox(16);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        notifListBox = new VBox(10);
        VBox notifCard = buildNotifListCard(controller, user);
        rightCol.getChildren().add(notifCard);

        content.getChildren().addAll(leftCol, rightCol);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F1F5F9; -fx-background: #F1F5F9;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("ColocManager — Notifications");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane buildHeader(User user, NotificationController controller) {
        Circle deco1 = new Circle(100);
        deco1.setFill(Color.web("#FFFFFF", 0.05));
        deco1.setTranslateX(500);
        deco1.setTranslateY(-20);
        deco1.setEffect(new GaussianBlur(20));

        Circle deco2 = new Circle(60);
        deco2.setFill(Color.web("#FFFFFF", 0.04));
        deco2.setTranslateX(700);
        deco2.setTranslateY(15);
        deco2.setEffect(new GaussianBlur(15));

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle(
                "-fx-background-color: rgba(255,255,255,0.12); -fx-text-fill: white;" +
                        "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;" +
                        "-fx-border-color: rgba(255,255,255,0.25); -fx-border-radius: 8;" +
                        "-fx-border-width: 1; -fx-font-size: 13px; -fx-font-weight: bold;"
        );
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        int unread = controller.getUnreadCount();
        StackPane badge = new StackPane();
        badge.setStyle(
                "-fx-background-color: " + (unread > 0 ? "#EF4444" : "#10B981") + ";" +
                        "-fx-background-radius: 20; -fx-padding: 4 14;"
        );
        Label badgeLbl = new Label(unread > 0 ? unread + " non lue(s)" : "Tout lu ✓");
        badgeLbl.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        badge.getChildren().add(badgeLbl);

        Text title = new Text("Notifications");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);

        Text subtitle = new Text(unread + " non lue(s)  •  " +
                controller.getNotifications().size() + " au total");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#FFFFFF", 0.7));

        HBox titleRow = new HBox(14, title, badge);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        VBox headerContent = new VBox(8, btnRetour, titleRow, subtitle);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, headerContent);
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E1B4B, #3730A3, #7C3AED);"
        );
        header.setPadding(new Insets(28, 40, 28, 40));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);
        return header;
    }

    private VBox buildStatsCard(NotificationController controller) {
        VBox card = buildCard("Statistiques");
        int total  = controller.getNotifications().size();
        int unread = controller.getUnreadCount();
        int read   = total - unread;
        card.getChildren().addAll(
                statRow("Total",    String.valueOf(total),  "#6366F1"),
                statRow("Non lues", String.valueOf(unread), "#EF4444"),
                statRow("Lues",     String.valueOf(read),   "#10B981")
        );
        return card;
    }

    private HBox statRow(String label, String value, String color) {
        StackPane iconBox = new StackPane();
        iconBox.setStyle(
                "-fx-background-color: " + color + "20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-min-width: 32; -fx-min-height: 32;" +
                        "-fx-max-width: 32; -fx-max-height: 32;"
        );
        Label iconLbl = new Label("●");
        iconLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + color + ";");
        iconBox.getChildren().add(iconLbl);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        HBox row = new HBox(10, iconBox, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10;");
        return row;
    }

    private VBox buildActionsCard(User user, NotificationController controller) {
        VBox card = buildCard("Actions");

        Button btnMarkAll = new Button("✓  Tout marquer comme lu");
        btnMarkAll.setMaxWidth(Double.MAX_VALUE);
        btnMarkAll.setStyle(
                "-fx-background-color: linear-gradient(to right, #6366F1, #8B5CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.3), 8, 0, 0, 3);"
        );
        btnMarkAll.setOnAction(e -> {
            Label lbl = new Label();
            controller.handleMarkAllAsRead(lbl);
            SceneManager.showNotifications(user);
        });

        Button btnDeleteSelected = new Button("✗  Supprimer la sélection");
        btnDeleteSelected.setMaxWidth(Double.MAX_VALUE);
        btnDeleteSelected.setStyle(
                "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                        "-fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20;"
        );
        btnDeleteSelected.setOnAction(e -> {
            if (selectedNotifs.isEmpty()) return;
            for (Notification n : selectedNotifs) {
                MainApp.notificationService.deleteNotification(n.getId());
            }
            selectedNotifs.clear();
            SceneManager.showNotifications(user);
        });

        Button btnSelectAll = new Button("☐  Tout sélectionner");
        btnSelectAll.setMaxWidth(Double.MAX_VALUE);
        btnSelectAll.setStyle(
                "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;" +
                        "-fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20;"
        );
        btnSelectAll.setOnAction(e -> {
            List<Notification> all = controller.getNotifications();
            selectedNotifs.clear();
            selectedNotifs.addAll(all);
            // Rafraichir visuellement toutes les rows
            refreshNotifRows(notifListBox, all, user);
            btnSelectAll.setText("☑  Tout désélectionner");
        });

        card.getChildren().addAll(btnMarkAll, btnSelectAll, btnDeleteSelected);
        return card;
    }

    private VBox buildNotifListCard(NotificationController controller, User user) {
        VBox card = buildCard("Toutes les notifications");

        List<Notification> notifications = controller.getNotifications();

        if (notifications.isEmpty()) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(40));

            StackPane emptyIcon = new StackPane();
            emptyIcon.setStyle(
                    "-fx-background-color: #F3F4F6; -fx-background-radius: 50;" +
                            "-fx-min-width: 80; -fx-min-height: 80;" +
                            "-fx-max-width: 80; -fx-max-height: 80;"
            );
            Label emptyLbl = new Label("!");
            emptyLbl.setStyle("-fx-font-size: 36px; -fx-text-fill: #D1D5DB; -fx-font-weight: bold;");
            emptyIcon.getChildren().add(emptyLbl);

            Label emptyText = new Label("Aucune notification");
            emptyText.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 15px; -fx-font-weight: bold;");

            Label emptySub = new Label("Vous êtes complètement à jour !");
            emptySub.setStyle("-fx-text-fill: #D1D5DB; -fx-font-size: 13px;");

            empty.getChildren().addAll(emptyIcon, emptyText, emptySub);
            notifListBox.getChildren().add(empty);
        } else {
            refreshNotifRows(notifListBox, notifications, user);
        }

        card.getChildren().add(notifListBox);
        return card;
    }

    private void refreshNotifRows(VBox box, List<Notification> notifications, User user) {
        box.getChildren().clear();
        for (Notification n : notifications) {
            box.getChildren().add(buildNotifRow(n, user, notifications));
        }
    }

    private VBox buildNotifRow(Notification n, User user, List<Notification> allNotifs) {
        boolean isRead     = n.isRead();
        boolean isSelected = selectedNotifs.contains(n);

        String typeIcon, typeColor, typeBg;
        switch (n.getType().name()) {
            case "TASK_ASSIGNED"  -> { typeIcon = "≡"; typeColor = "#6366F1"; typeBg = "#EEF2FF"; }
            case "TASK_VALIDATED" -> { typeIcon = "✓"; typeColor = "#10B981"; typeBg = "#D1FAE5"; }
            case "TASK_REJECTED"  -> { typeIcon = "✗"; typeColor = "#EF4444"; typeBg = "#FEE2E2"; }
            default               -> { typeIcon = "!"; typeColor = "#F59E0B"; typeBg = "#FEF3C7"; }
        }

        // Checkbox de sélection
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(isSelected);
        checkBox.setStyle("-fx-cursor: hand;");

        StackPane iconBox = new StackPane();
        iconBox.setStyle(
                "-fx-background-color: " + typeBg + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-min-width: 48; -fx-min-height: 48;" +
                        "-fx-max-width: 48; -fx-max-height: 48;"
        );
        Label iconLbl = new Label(typeIcon);
        iconLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + typeColor + ";");
        iconBox.getChildren().add(iconLbl);

        Label msgLbl = new Label(n.getMessage());
        msgLbl.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + (isRead ? "#6B7280" : "#1E1B4B") + ";" +
                        "-fx-font-weight: " + (isRead ? "normal" : "bold") + ";"
        );
        msgLbl.setWrapText(true);
        msgLbl.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(msgLbl, Priority.ALWAYS);

        Label statusLbl = new Label(isRead ? "Lu" : "Nouveau");
        statusLbl.setStyle(
                "-fx-background-color: " + (isRead ? "#F3F4F6" : "#EEF2FF") + ";" +
                        "-fx-text-fill: " + (isRead ? "#9CA3AF" : "#6366F1") + ";" +
                        "-fx-background-radius: 20; -fx-padding: 3 12;" +
                        "-fx-font-size: 10px; -fx-font-weight: bold;"
        );

        VBox row = new VBox();
        row.setPadding(new Insets(14, 16, 14, 16));

        // Style selon sélection
        String rowStyle = isSelected
                ? "-fx-background-color: #EEF2FF; -fx-background-radius: 12;" +
                "-fx-border-color: #6366F1; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;"
                : "-fx-background-color: " + (isRead ? "#F9FAFB" : "#EEF2FF") + ";" +
                "-fx-background-radius: 12; -fx-border-color: " + (isRead ? "#E5E7EB" : "#A5B4FC") + ";" +
                "-fx-border-radius: 12; -fx-border-width: 1; -fx-cursor: hand;";
        row.setStyle(rowStyle);

        HBox rowContent = new HBox(12, checkBox, iconBox, msgLbl, statusLbl);
        rowContent.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().add(rowContent);

        // Clic sur la row = toggle sélection
        row.setOnMouseClicked(e -> {
            if (selectedNotifs.contains(n)) {
                selectedNotifs.remove(n);
                checkBox.setSelected(false);
                row.setStyle(
                        "-fx-background-color: " + (isRead ? "#F9FAFB" : "#EEF2FF") + ";" +
                                "-fx-background-radius: 12; -fx-border-color: " + (isRead ? "#E5E7EB" : "#A5B4FC") + ";" +
                                "-fx-border-radius: 12; -fx-border-width: 1; -fx-cursor: hand;"
                );
            } else {
                selectedNotifs.add(n);
                checkBox.setSelected(true);
                row.setStyle(
                        "-fx-background-color: #EEF2FF; -fx-background-radius: 12;" +
                                "-fx-border-color: #6366F1; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;"
                );
            }
        });

        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                if (!selectedNotifs.contains(n)) selectedNotifs.add(n);
                row.setStyle(
                        "-fx-background-color: #EEF2FF; -fx-background-radius: 12;" +
                                "-fx-border-color: #6366F1; -fx-border-radius: 12; -fx-border-width: 2; -fx-cursor: hand;"
                );
            } else {
                selectedNotifs.remove(n);
                row.setStyle(
                        "-fx-background-color: " + (isRead ? "#F9FAFB" : "#EEF2FF") + ";" +
                                "-fx-background-radius: 12; -fx-border-color: " + (isRead ? "#E5E7EB" : "#A5B4FC") + ";" +
                                "-fx-border-radius: 12; -fx-border-width: 1; -fx-cursor: hand;"
                );
            }
        });

        return row;
    }

    private VBox buildCard(String title) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");
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
}