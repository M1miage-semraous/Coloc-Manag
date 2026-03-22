package com.colocmanager.repository;

import com.colocmanager.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public void save(User user) {
        users.add(user);
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public Optional<User> findById(UUID id) {
        return users.stream()
                .filter(user -> user.getId() != null && user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void delete(UUID id) {
        users.removeIf(user -> user.getId() != null && user.getId().equals(id));
    }
}