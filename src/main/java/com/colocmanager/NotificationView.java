package com.colocmanager;

import com.colocmanager.controller.NotificationController;
import com.colocmanager.model.Notification;
import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class NotificationView {

    private static final String NAVY  = "#1A2B4A";
    private static final String TEAL  = "#0D9488";
    private static final String GREY  = "#F8FAFC";
    private static final String SLATE = "#64748B";

    public NotificationView(Stage stage, User user) {
        NotificationController controller = new NotificationController(user);
        build(stage, user, controller);
    }

    private void build(Stage stage, User user, NotificationController controller) {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(28));
        pane.setStyle("-fx-background-color: " + GREY + ";");

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: transparent; -fx-text-fill: " + NAVY + "; -fx-font-size: 13; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        Text titre = new Text("Notifications");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        Label lblCount = new Label("Non lues : " + controller.getUnreadCount());
        lblCount.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13px;");

        Label lblResult = new Label();
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12px;");

        Button btnMarkAll = new Button("✓ Tout marquer comme lu");
        btnMarkAll.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        btnMarkAll.setOnAction(e -> {
            controller.handleMarkAllAsRead(lblResult);
            lblCount.setText("Non lues : 0");
        });

        pane.getChildren().addAll(btnRetour, titre, lblCount, btnMarkAll, lblResult);

        List<Notification> notifications = controller.getNotifications();

        if (notifications.isEmpty()) {
            Label empty = new Label("Aucune notification.");
            empty.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13px;");
            pane.getChildren().add(empty);
        } else {
            for (Notification n : notifications) {
                VBox card = new VBox(4);
                card.setPadding(new Insets(12, 16, 12, 16));
                card.setStyle(
                        "-fx-background-color: " + (n.isRead() ? "white" : "#E6FFF8") + ";" +
                                "-fx-border-color: " + (n.isRead() ? "#E2E8F0" : TEAL) + ";" +
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;"
                );

                Label lblStatut = new Label(n.isRead() ? "✅ Lu" : "🔔 Nouveau");
                lblStatut.setStyle("-fx-text-fill: " + (n.isRead() ? SLATE : TEAL) + "; -fx-font-size: 11px;");

                Label lblTitre = new Label(n.getTitle());
                lblTitre.setStyle("-fx-text-fill: " + NAVY + "; -fx-font-size: 13px; -fx-font-weight: bold;");

                Label lblMessage = new Label(n.getMessage());
                lblMessage.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 12px;");
                lblMessage.setWrapText(true);
                lblMessage.setMaxWidth(Double.MAX_VALUE);

                card.getChildren().addAll(lblStatut, lblTitre, lblMessage);
                pane.getChildren().add(card);
            }
        }

        Scene scene = new Scene(pane, 1100, 700);
        stage.setTitle("ColocManager - Notifications");
        stage.setScene(scene);
        stage.show();
    }
}