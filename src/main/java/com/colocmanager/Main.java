package com.colocmanager;

import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.Role;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import com.colocmanager.repository.TaskRepository;
import com.colocmanager.repository.TaskValidationRepository;
import com.colocmanager.service.TaskService;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        TaskRepository taskRepository = new TaskRepository();
        TaskValidationRepository taskValidationRepository = new TaskValidationRepository();

        TaskService taskService = new TaskService(taskRepository, taskValidationRepository);

        User admin = new User("Admin", "admin@gmail.com", "1234", Role.ADMIN);
        User alice = new User("Alice", "alice@gmail.com", "1234", Role.USER);

        Task task = taskService.createTask(
                "Nettoyer la cuisine",
                "Nettoyer complètement la cuisine",
                LocalDate.now().plusDays(1),
                ImportanceLevel.HIGH,
                5,
                alice,
                admin
        );

        System.out.println("Tâche créée : " + task.getTitle());
        System.out.println("Priorité calculée : " + task.getCalculatedPriority());

        taskService.startTask(task.getId());
        System.out.println("Statut après démarrage : " + task.getStatus());

        taskService.markTaskCompleted(task.getId());
        System.out.println("Statut après fin : " + task.getStatus());

        taskService.validateTask(task.getId(), admin, "Très bon travail");
        System.out.println("Statut après validation : " + task.getStatus());

        System.out.println("Nombre de notifications de Alice : " + alice.getNotifications());


    }
}