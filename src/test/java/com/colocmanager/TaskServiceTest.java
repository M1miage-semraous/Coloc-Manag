package com.colocmanager;

import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.Role;
import com.colocmanager.enums.TaskStatus;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import com.colocmanager.repository.NotificationRepository;
import com.colocmanager.repository.TaskRepository;
import com.colocmanager.repository.TaskValidationRepository;
import com.colocmanager.repository.UserRepository;
import com.colocmanager.service.TaskService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Tests unitaires — TaskService")
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private TaskValidationRepository validationRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;

    private TaskService taskService;
    private User admin;
    private User alice;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, validationRepository, notificationRepository, userRepository);

        admin = new User("Adnan", "adnan@test.com", "pass", Role.ADMIN);
        admin.setId(UUID.randomUUID());

        alice = new User("Alice", "alice@test.com", "pass", Role.USER);
        alice.setId(UUID.randomUUID());

        when(userRepository.findAll()).thenReturn(List.of(admin, alice));
    }

    @Test
    @DisplayName("Créer une tâche avec des données valides")
    void testCreateTask_Success() {
        Task task = taskService.createTask(
                "Nettoyer la cuisine", "Nettoyage complet",
                LocalDate.now().plusDays(7),
                ImportanceLevel.MEDIUM, 2, alice, admin
        );

        assertNotNull(task);
        assertEquals("Nettoyer la cuisine", task.getTitle());
        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertEquals(alice, task.getAssignedUser());
        assertEquals(admin, task.getCreatedBy());

        verify(taskRepository, times(1)).save(task);
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Créer une tâche sans utilisateur assigné")
    void testCreateTask_NoAssignedUser() {
        Task task = taskService.createTask(
                "Tâche sans assignation", "",
                LocalDate.now().plusDays(3),
                ImportanceLevel.LOW, 1, null, admin
        );

        assertNotNull(task);
        assertNull(task.getAssignedUser());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Créer une tâche HIGH importance avec 4h+ doit être URGENT")
    void testCreateTask_UrgentPriority() {
        Task task = taskService.createTask(
                "Tâche urgente", "",
                LocalDate.now().plusDays(1),
                ImportanceLevel.HIGH, 5, alice, admin
        );

        assertEquals(com.colocmanager.enums.PriorityLevel.URGENT, task.getCalculatedPriority());
    }

    @Test
    @DisplayName("Démarrer une tâche change son statut à IN_PROGRESS")
    void testStartTask_Success() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test", "", LocalDate.now().plusDays(5), ImportanceLevel.MEDIUM, 1, alice, admin);
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean result = taskService.startTask(taskId);

        assertTrue(result);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Marquer une tâche comme terminée change son statut à PENDING_VALIDATION")
    void testMarkTaskCompleted_Success() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test", "", LocalDate.now().plusDays(5), ImportanceLevel.MEDIUM, 1, alice, admin);
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean result = taskService.markTaskCompleted(taskId);

        assertTrue(result);
        assertEquals(TaskStatus.PENDING_VALIDATION, task.getStatus());
        verify(taskRepository, times(1)).save(task);
        verify(notificationRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("Valider une tâche change son statut à VALIDATED")
    void testValidateTask_Success() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test", "", LocalDate.now().plusDays(5), ImportanceLevel.MEDIUM, 1, alice, admin);
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean result = taskService.validateTask(taskId, admin, "Bien fait");

        assertTrue(result);
        assertEquals(TaskStatus.VALIDATED, task.getStatus());
        verify(taskRepository, times(1)).save(task);
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Rejeter une tâche change son statut à REJECTED")
    void testRejectTask_Success() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test", "", LocalDate.now().plusDays(5), ImportanceLevel.MEDIUM, 1, alice, admin);
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean result = taskService.rejectTask(taskId, admin, "À refaire");

        assertTrue(result);
        assertEquals(TaskStatus.REJECTED, task.getStatus());
        verify(taskRepository, times(1)).save(task);
        verify(notificationRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Opérations sur une tâche inexistante retournent false")
    void testOperations_TaskNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(taskRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertFalse(taskService.startTask(fakeId));
        assertFalse(taskService.markTaskCompleted(fakeId));
        assertFalse(taskService.validateTask(fakeId, admin, ""));
        assertFalse(taskService.rejectTask(fakeId, admin, ""));
        assertFalse(taskService.deleteTask(fakeId));
    }

    @Test
    @DisplayName("Récupérer toutes les tâches")
    void testGetAllTasks() {
        List<Task> tasks = List.of(
                new Task("T1", "", LocalDate.now(), ImportanceLevel.LOW, 1, alice, admin),
                new Task("T2", "", LocalDate.now(), ImportanceLevel.HIGH, 2, alice, admin)
        );
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Récupérer les tâches d'un utilisateur")
    void testGetTasksByAssignedUser() {
        List<Task> tasks = List.of(
                new Task("T1", "", LocalDate.now(), ImportanceLevel.LOW, 1, alice, admin)
        );
        when(taskRepository.findByAssignedUser(alice)).thenReturn(tasks);

        List<Task> result = taskService.getTasksByAssignedUser(alice);

        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByAssignedUser(alice);
    }

    @Test
    @DisplayName("Supprimer une tâche existante")
    void testDeleteTask_Success() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test", "", LocalDate.now(), ImportanceLevel.LOW, 1, alice, admin);
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean result = taskService.deleteTask(taskId);

        assertTrue(result);
        verify(taskRepository, times(1)).delete(taskId);
    }
}