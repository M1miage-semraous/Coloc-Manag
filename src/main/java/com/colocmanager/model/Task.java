package com.colocmanager.model;

import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.PriorityLevel;
import com.colocmanager.enums.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;
import  java.time.LocalDateTime;

public class Task {

    private UUID id;
    private String title;
    private LocalDateTime validatedAt;
    private LocalDate deadline;
    private ImportanceLevel importance;
    private int estimatedTimeHours;
    private PriorityLevel calculatedPiority;
    private TaskStatus status;
    private LocalDateTime createdAt;



}












