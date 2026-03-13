package com.colocmanager.model;

import com.colocmanager.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime createdAt;

}