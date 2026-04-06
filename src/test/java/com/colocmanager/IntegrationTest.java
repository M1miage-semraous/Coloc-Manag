package com.colocmanager;

import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.Role;
import com.colocmanager.enums.TaskStatus;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import com.colocmanager.repository.*;
import com.colocmanager.service.ExpenseService;
import com.colocmanager.service.NotificationService;
import com.colocmanager.service.TaskService;
import com.colocmanager.service.UserService;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests d'intégration — Base de données SQLite")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    private static Connection connection;
    private static UserService userService;
    private static TaskService taskService;
    private static ExpenseService expenseService;
    private static NotificationService notificationService;

    private static User admin;
    private static User alice;
    private static User bob;

    @BeforeAll
    static void setUpDatabase() throws Exception {
        // Base SQLite en mémoire pour les tests
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        createTables();

        UserRepository userRepo = new UserRepository(connection);
        TaskRepository taskRepo = new TaskRepository(connection);
        TaskValidationRepository valRepo = new TaskValidationRepository(connection);
        ExpenseRepository expenseRepo = new ExpenseRepository(connection);
        ExpenseShareRepository shareRepo = new ExpenseShareRepository(connection);
        NotificationRepository notifRepo = new NotificationRepository(connection);

        userService = new UserService(userRepo);
        taskService = new TaskService(taskRepo, valRepo, notifRepo, userRepo);
        expenseService = new ExpenseService(expenseRepo, shareRepo);
        notificationService = new NotificationService(notifRepo);
    }

    static void createTables() throws Exception {
        Statement stmt = connection.createStatement();
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY, full_name TEXT, email TEXT UNIQUE,
                password TEXT, role TEXT, created_at TEXT)
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                id TEXT PRIMARY KEY, title TEXT, description TEXT,
                status TEXT, priority TEXT, importance TEXT,
                estimated_hours REAL, deadline TEXT, created_at TEXT,
                creator_id TEXT, assigned_to_id TEXT)
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS notifications (
                id TEXT PRIMARY KEY, title TEXT, message TEXT,
                type TEXT, is_read INTEGER, created_at TEXT, user_id TEXT)
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS expenses (
                id TEXT PRIMARY KEY, description TEXT, amount REAL,
                date TEXT, paid_by_id TEXT)
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS expense_shares (
                id TEXT PRIMARY KEY, expense_id TEXT, user_id TEXT,
                amount REAL, is_paid INTEGER)
        """);
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS task_validations (
                id TEXT PRIMARY KEY, task_id TEXT, validator_id TEXT,
                decision TEXT, comment TEXT, validated_at TEXT)
        """);
        stmt.close();
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (connection != null) connection.close();
    }

    // ===== TESTS UTILISATEURS =====

    @Test
    @Order(1)
    @DisplayName("IT-01 : Créer des utilisateurs en base")
    void testCreateUsers() {
        admin = userService.createUser("Adnan", "adnan@test.com", "1234", Role.ADMIN);
        alice = userService.createUser("Alice", "alice@test.com", "1234", Role.USER);
        bob   = userService.createUser("Bob",   "bob@test.com",   "1234", Role.USER);

        assertNotNull(admin.getId());
        assertNotNull(alice.getId());
        assertNotNull(bob.getId());

        List<User> users = userService.getAllUsers();
        assertEquals(3, users.size());
    }

    @Test
    @Order(2)
    @DisplayName("IT-02 : Connexion avec mot de passe BCrypt")
    void testLogin() {
        Optional<User> result = userService.login("adnan@test.com", "1234");
        assertTrue(result.isPresent());
        assertEquals("Adnan", result.get().getFullName());
    }

    @Test
    @Order(3)
    @DisplayName("IT-03 : Connexion avec mauvais mot de passe")
    void testLoginWrongPassword() {
        Optional<User> result = userService.login("adnan@test.com", "wrongpass");
        assertFalse(result.isPresent());
    }

    // ===== TESTS TÂCHES =====

    @Test
    @Order(4)
    @DisplayName("IT-04 : Créer une tâche et vérifier en base")
    void testCreateTask() {
        admin = userService.findByEmail("adnan@test.com").get();
        alice = userService.findByEmail("alice@test.com").get();

        Task task = taskService.createTask(
                "Nettoyer cuisine", "Description",
                LocalDate.now().plusDays(7),
                ImportanceLevel.HIGH, 2, alice, admin
        );

        assertNotNull(task.getId());
        assertEquals(TaskStatus.TO_DO, task.getStatus());

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Nettoyer cuisine", tasks.get(0).getTitle());
    }

    @Test
    @Order(5)
    @DisplayName("IT-05 : Workflow complet d'une tâche")
    void testTaskWorkflow() {
        Task task = taskService.getAllTasks().get(0);

        // Démarrer
        taskService.startTask(task.getId());
        Task updated = taskService.findById(task.getId()).get();
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());

        // Terminer
        taskService.markTaskCompleted(task.getId());
        updated = taskService.findById(task.getId()).get();
        assertEquals(TaskStatus.PENDING_VALIDATION, updated.getStatus());

        // Valider
        admin = userService.findByEmail("adnan@test.com").get();
        taskService.validateTask(task.getId(), admin, "Très bien");
        updated = taskService.findById(task.getId()).get();
        assertEquals(TaskStatus.VALIDATED, updated.getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("IT-06 : Notification créée après assignation")
    void testNotificationOnAssignment() {
        admin = userService.findByEmail("adnan@test.com").get();
        alice = userService.findByEmail("alice@test.com").get();

        taskService.createTask(
                "Nouvelle tâche", "",
                LocalDate.now().plusDays(3),
                ImportanceLevel.MEDIUM, 1, alice, admin
        );

        List<com.colocmanager.model.Notification> notifs =
                notificationService.getNotificationsByUser(alice);

        assertFalse(notifs.isEmpty());
    }

    // ===== TESTS DÉPENSES =====

    @Test
    @Order(7)
    @DisplayName("IT-07 : Créer une dépense et vérifier la répartition")
    void testCreateExpense() {
        admin = userService.findByEmail("adnan@test.com").get();
        alice = userService.findByEmail("alice@test.com").get();
        bob   = userService.findByEmail("bob@test.com").get();

        List<User> participants = List.of(admin, alice, bob);

        expenseService.createExpense(
                "Courses", 30.0, "", LocalDate.now(), admin, participants
        );

        List<com.colocmanager.model.Expense> expenses = expenseService.getAllExpenses();
        assertFalse(expenses.isEmpty());

        com.colocmanager.model.Expense expense = expenses.get(0);
        assertEquals(30.0, expense.getAmount(), 0.01);
        assertEquals(3, expense.getShares().size());
        expense.getShares().forEach(s ->
                assertEquals(10.0, s.getAmountDue(), 0.01)
        );
    }

    @Test
    @Order(8)
    @DisplayName("IT-08 : Marquer une part comme payée")
    void testMarkShareAsPaid() {
        alice = userService.findByEmail("alice@test.com").get();

        List<com.colocmanager.model.ExpenseShare> shares =
                expenseService.getSharesForUser(alice.getId());

        assertFalse(shares.isEmpty());

        com.colocmanager.model.ExpenseShare share = shares.get(0);
        assertFalse(share.isPaid());

        expenseService.markShareAsPaid(share.getId());

        double remaining = expenseService.getTotalDueByUser(alice.getId());
        assertEquals(0.0, remaining, 0.01);
    }

    @Test
    @Order(9)
    @DisplayName("IT-09 : Supprimer une dépense supprime ses parts")
    void testDeleteExpenseCascade() {
        admin = userService.findByEmail("adnan@test.com").get();
        alice = userService.findByEmail("alice@test.com").get();

        com.colocmanager.model.Expense expense = expenseService.createExpense(
                "À supprimer", 20.0, "", LocalDate.now(), admin, List.of(admin, alice)
        );

        int beforeCount = expenseService.getAllExpenses().size();
        expenseService.deleteExpense(expense.getId());
        int afterCount = expenseService.getAllExpenses().size();

        assertEquals(beforeCount - 1, afterCount);
    }

    @Test
    @Order(10)
    @DisplayName("IT-10 : Supprimer un utilisateur")
    void testDeleteUser() {
        bob = userService.findByEmail("bob@test.com").get();

        int before = userService.getAllUsers().size();
        userService.deleteUser(bob.getId());
        int after = userService.getAllUsers().size();

        assertEquals(before - 1, after);
        assertFalse(userService.findByEmail("bob@test.com").isPresent());
    }
}
