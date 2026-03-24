package com.colocmanager.service;

import com.colocmanager.enums.Role;
import com.colocmanager.model.User;
import com.colocmanager.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String fullName, String email, String password, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà.");
        }

        User user = new User(fullName, email, password, role);
        userRepository.save(user);
        return user;
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(UUID userId) {
        Optional<User> existingUser = userRepository.findById(userId);

        if (existingUser.isEmpty()) {
            return false;
        }

        userRepository.delete(userId);
        return true;
    }

    public boolean updateUser(UUID userId, String fullName, String email, String password, Role role) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new IllegalArgumentException("Un autre utilisateur utilise déjà cet email.");
            }
        }

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            user.setPassword(password);
        }

        if (role != null) {
            user.setRole(role);
        }

        return true;
    }

    public Optional<User> login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}