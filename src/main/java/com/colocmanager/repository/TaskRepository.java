package com.colocmanager.repository;

import com.colocmanager.model.MonthlyReport;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    public void save(Task task) {
        tasks.add(task);
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Optional<Task> findById(UUID id) {
        return tasks.stream()
                .filter(task -> task.getId() != null && task.getId().equals(id))
                .findFirst();
    }

    public List<Task> findByAssignedUser(User user) {
        return tasks.stream()
                .filter(task -> task.getAssignedUser() != null
                        && task.getAssignedUser().getId() != null
                        && user != null
                        && user.getId() != null
                        && task.getAssignedUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    //changed
    public List<Task> findByMonth(int month, int year) {
        return tasks.stream()
                .filter(r -> r.getMonth() == month
                        && r.getYear() == year)
                .collect(Collectors.toList());
    }
    public List<Task> findByCreatedBy(User user) {
        return tasks.stream()
                .filter(task -> task.getCreatedBy() != null
                        && task.getCreatedBy().getId() != null
                        && user != null
                        && user.getId() != null
                        && task.getCreatedBy().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        tasks.removeIf(task -> task.getId() != null && task.getId().equals(id));
    }
}