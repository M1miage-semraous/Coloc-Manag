package com.colocmanager;

import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.Role;
import com.colocmanager.model.*;
import com.colocmanager.repository.*;
import com.colocmanager.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static UserService userService;
    static TaskService taskService;
    static ExpenseService expenseService;
    static NotificationService notificationService;

    static User currentUser = null;

    public static void main(String[] args) {

        // --- Setup repositories ---
        UserRepository userRepo = new UserRepository();
        TaskRepository taskRepo = new TaskRepository();
        TaskValidationRepository validationRepo = new TaskValidationRepository();
        ExpenseRepository expenseRepo = new ExpenseRepository();
        ExpenseShareRepository shareRepo = new ExpenseShareRepository();
        NotificationRepository notifRepo = new NotificationRepository();

        // --- Setup services ---
        userService = new UserService(userRepo);
        taskService = new TaskService(taskRepo, validationRepo);
        expenseService = new ExpenseService(expenseRepo, shareRepo);
        notificationService = new NotificationService(notifRepo);

        // --- Données de démo ---
        userService.createUser("Adnan", "adnan@gmail.com", "1234", Role.ADMIN);
        userService.createUser("Alice", "alice@gmail.com", "1234", Role.USER);
        userService.createUser("Bob",   "bob@gmail.com",   "1234", Role.USER);

        // --- Lancement ---
        menuLogin();
    }

    // ─── LOGIN ────────────────────────────────────────────────────

    static void menuLogin() {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║      ColocManager        ║");
            System.out.println("╠══════════════════════════╣");
            System.out.println("║  1. Se connecter         ║");
            System.out.println("║  0. Quitter              ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Choix : ");

            int choix = lireInt();
            switch (choix) {
                case 1 -> login();
                case 0 -> { System.out.println("Au revoir !"); System.exit(0); }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    static void login() {
        System.out.print("Email    : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        Optional<User> user = userService.login(email, password);

        if (user.isPresent()) {
            currentUser = user.get();
            System.out.println("\n✓ Connecté en tant que : " + currentUser.getFullName()
                    + " (" + currentUser.getRole() + ")");
            menuPrincipal();
        } else {
            System.out.println("✗ Email ou mot de passe incorrect.");
        }
    }

    // ─── MENU PRINCIPAL ───────────────────────────────────────────

    static void menuPrincipal() {
        while (true) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║  Bonjour, " + currentUser.getFullName());
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  1. Gérer les tâches         ║");
            System.out.println("║  2. Gérer les dépenses       ║");
            System.out.println("║  3. Mes notifications        ║");
            if (currentUser.getRole() == Role.ADMIN) {
                System.out.println("║  4. Gérer les utilisateurs   ║");
            }
            System.out.println("║  0. Se déconnecter           ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Choix : ");

            int choix = lireInt();
            switch (choix) {
                case 1 -> menuTaches();
                case 2 -> menuDepenses();
                case 3 -> afficherNotifications();
                case 4 -> {
                    if (currentUser.getRole() == Role.ADMIN) menuUtilisateurs();
                    else System.out.println("Accès refusé.");
                }
                case 0 -> { currentUser = null; return; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ─── MENU TÂCHES ──────────────────────────────────────────────

    static void menuTaches() {
        while (true) {
            System.out.println("\n--- Tâches ---");
            System.out.println("1. Voir toutes les tâches");
            System.out.println("2. Créer une tâche");
            System.out.println("3. Démarrer une tâche");
            System.out.println("4. Marquer une tâche comme terminée");
            System.out.println("5. Valider une tâche (ADMIN)");
            System.out.println("6. Rejeter une tâche (ADMIN)");
            System.out.println("0. Retour");
            System.out.print("Choix : ");

            int choix = lireInt();
            switch (choix) {
                case 1 -> afficherTaches();
                case 2 -> creerTache();
                case 3 -> demarrerTache();
                case 4 -> terminerTache();
                case 5 -> validerTache();
                case 6 -> rejeterTache();
                case 0 -> { return; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    static void afficherTaches() {
        List<Task> taches = taskService.getAllTasks();
        if (taches.isEmpty()) {
            System.out.println("Aucune tâche.");
            return;
        }
        System.out.println("\n── Liste des tâches ──");
        for (Task t : taches) {
            System.out.println("  [" + t.getId().toString().substring(0, 8) + "]"
                    + "  " + t.getTitle()
                    + "  |  " + t.getStatus()
                    + "  |  Priorité: " + t.getCalculatedPriority()
                    + "  |  Assigné à: " + (t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : "—")
                    + "  |  Deadline: " + t.getDeadline());
        }
    }

    static void creerTache() {
        System.out.print("Titre : ");
        String titre = scanner.nextLine();
        System.out.print("Description : ");
        String desc = scanner.nextLine();
        System.out.print("Deadline (dans combien de jours) : ");
        int jours = lireInt();
        System.out.print("Importance (LOW / MEDIUM / HIGH) : ");
        String imp = scanner.nextLine().toUpperCase();
        System.out.print("Durée estimée (heures) : ");
        int heures = lireInt();

        System.out.println("Assigner à :");
        List<User> users = userService.getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + users.get(i).getFullName());
        }
        System.out.print("Numéro : ");
        int idx = lireInt() - 1;

        if (idx < 0 || idx >= users.size()) {
            System.out.println("Utilisateur invalide.");
            return;
        }

        try {
            Task task = taskService.createTask(
                    titre, desc,
                    LocalDate.now().plusDays(jours),
                    ImportanceLevel.valueOf(imp),
                    heures,
                    users.get(idx),
                    currentUser
            );
            System.out.println("✓ Tâche créée : " + task.getTitle()
                    + " | Priorité calculée : " + task.getCalculatedPriority());
        } catch (Exception e) {
            System.out.println("✗ Erreur : " + e.getMessage());
        }
    }

    static void demarrerTache() {
        System.out.print("ID de la tâche (8 premiers caractères) : ");
        String idDebut = scanner.nextLine();
        Task task = trouverTacheParIdCourt(idDebut);
        if (task == null) return;

        boolean ok = taskService.startTask(task.getId());
        System.out.println(ok ? "✓ Tâche démarrée." : "✗ Impossible de démarrer.");
    }

    static void terminerTache() {
        System.out.print("ID de la tâche (8 premiers caractères) : ");
        String idDebut = scanner.nextLine();
        Task task = trouverTacheParIdCourt(idDebut);
        if (task == null) return;

        boolean ok = taskService.markTaskCompleted(task.getId());
        System.out.println(ok ? "✓ Tâche marquée comme terminée (en attente de validation)." : "✗ Erreur.");
    }

    static void validerTache() {
        if (currentUser.getRole() != Role.ADMIN) {
            System.out.println("✗ Réservé à l'admin.");
            return;
        }
        System.out.print("ID de la tâche (8 premiers caractères) : ");
        String idDebut = scanner.nextLine();
        Task task = trouverTacheParIdCourt(idDebut);
        if (task == null) return;

        System.out.print("Commentaire : ");
        String comment = scanner.nextLine();

        boolean ok = taskService.validateTask(task.getId(), currentUser, comment);
        System.out.println(ok ? "✓ Tâche validée !" : "✗ Erreur.");
    }

    static void rejeterTache() {
        if (currentUser.getRole() != Role.ADMIN) {
            System.out.println("✗ Réservé à l'admin.");
            return;
        }
        System.out.print("ID de la tâche (8 premiers caractères) : ");
        String idDebut = scanner.nextLine();
        Task task = trouverTacheParIdCourt(idDebut);
        if (task == null) return;

        System.out.print("Raison du rejet : ");
        String comment = scanner.nextLine();

        boolean ok = taskService.rejectTask(task.getId(), currentUser, comment);
        System.out.println(ok ? "✓ Tâche rejetée." : "✗ Erreur.");
    }

    // ─── MENU DÉPENSES ────────────────────────────────────────────

    static void menuDepenses() {
        while (true) {
            System.out.println("\n--- Dépenses ---");
            System.out.println("1. Voir toutes les dépenses");
            System.out.println("2. Ajouter une dépense");
            System.out.println("3. Mon solde (ce que je dois)");
            System.out.println("0. Retour");
            System.out.print("Choix : ");

            int choix = lireInt();
            switch (choix) {
                case 1 -> afficherDepenses();
                case 2 -> ajouterDepense();
                case 3 -> afficherSolde();
                case 0 -> { return; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    static void afficherDepenses() {
        List<Expense> depenses = expenseService.getAllExpenses();
        if (depenses.isEmpty()) {
            System.out.println("Aucune dépense.");
            return;
        }
        System.out.println("\n── Liste des dépenses ──");
        for (Expense e : depenses) {
            System.out.println("  " + e.getLabel()
                    + "  |  " + e.getAmount() + "€"
                    + "  |  Payé par: " + e.getPaidBy().getFullName()
                    + "  |  Date: " + e.getExpenseDate());
            for (ExpenseShare share : e.getShares()) {
                System.out.println("      → " + share.getUser().getFullName()
                        + " doit : " + String.format("%.2f", share.getAmountDue()) + "€");
            }
        }
    }

    static void ajouterDepense() {
        System.out.print("Libellé : ");
        String label = scanner.nextLine();
        System.out.print("Montant (€) : ");
        double montant = lireDouble();
        System.out.print("Description : ");
        String desc = scanner.nextLine();

        List<User> users = userService.getAllUsers();
        System.out.println("Participants (séparés par virgule, ex: 1,2,3) :");
        for (int i = 0; i < users.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + users.get(i).getFullName());
        }
        System.out.print("Numéros : ");
        String[] nums = scanner.nextLine().split(",");

        List<User> participants = new java.util.ArrayList<>();
        for (String n : nums) {
            try {
                int idx = Integer.parseInt(n.trim()) - 1;
                if (idx >= 0 && idx < users.size()) participants.add(users.get(idx));
            } catch (NumberFormatException ignored) {}
        }

        if (participants.isEmpty()) {
            System.out.println("✗ Aucun participant valide.");
            return;
        }

        Expense expense = expenseService.createExpense(
                label, montant, desc, LocalDate.now(), currentUser, participants
        );
        System.out.println("✓ Dépense ajoutée : " + expense.getLabel()
                + " | Part par personne : "
                + String.format("%.2f", montant / participants.size()) + "€");
    }

    static void afficherSolde() {
        double totalDu = expenseService.getTotalDueByUser(currentUser.getId());
        System.out.println("\n── Mon solde ──");
        System.out.println("  Total que je dois : " + String.format("%.2f", totalDu) + "€");
    }

    // ─── NOTIFICATIONS ────────────────────────────────────────────

    static void afficherNotifications() {
        List<Notification> notifs = currentUser.getNotifications();
        if (notifs.isEmpty()) {
            System.out.println("Aucune notification.");
            return;
        }
        System.out.println("\n── Notifications ──");
        for (Notification n : notifs) {
            String lu = n.isRead() ? "[LU]" : "[NEW]";
            System.out.println("  " + lu + "  " + n.getTitle() + " : " + n.getMessage());
        }
    }

    // ─── UTILISATEURS (ADMIN) ─────────────────────────────────────

    static void menuUtilisateurs() {
        while (true) {
            System.out.println("\n--- Utilisateurs ---");
            System.out.println("1. Voir tous les utilisateurs");
            System.out.println("2. Ajouter un utilisateur");
            System.out.println("0. Retour");
            System.out.print("Choix : ");

            int choix = lireInt();
            switch (choix) {
                case 1 -> afficherUtilisateurs();
                case 2 -> ajouterUtilisateur();
                case 0 -> { return; }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    static void afficherUtilisateurs() {
        List<User> users = userService.getAllUsers();
        System.out.println("\n── Utilisateurs ──");
        for (User u : users) {
            System.out.println("  " + u.getFullName()
                    + "  |  " + u.getEmail()
                    + "  |  " + u.getRole());
        }
    }

    static void ajouterUtilisateur() {
        System.out.print("Nom complet : ");
        String nom = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine();
        System.out.print("Rôle (USER / ADMIN) : ");
        String role = scanner.nextLine().toUpperCase();

        try {
            User u = userService.createUser(nom, email, mdp, Role.valueOf(role));
            System.out.println("✓ Utilisateur créé : " + u.getFullName());
        } catch (Exception e) {
            System.out.println("✗ Erreur : " + e.getMessage());
        }
    }

    // ─── UTILITAIRES ──────────────────────────────────────────────

    static Task trouverTacheParIdCourt(String idDebut) {
        return taskService.getAllTasks().stream()
                .filter(t -> t.getId().toString().startsWith(idDebut))
                .findFirst()
                .orElseGet(() -> {
                    System.out.println("✗ Tâche introuvable.");
                    return null;
                });
    }

    static int lireInt() {
        try {
            String line = scanner.nextLine();
            return Integer.parseInt(line.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static double lireDouble() {
        try {
            String line = scanner.nextLine();
            return Double.parseDouble(line.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}