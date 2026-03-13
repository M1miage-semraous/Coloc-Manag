package com.colocmanager.model;

import com.colocmanager.enums.NotificationType;
import java.util.UUID;
import  java.time.LocalDateTime;

public class MonthlyReport {

    private UUID id;
    private int month;
    private int year;
    private int totalTasksAssigned;
    private int totalTasksValidated;
    private int TotalTasksRejected;
    double totalPaid;
    double totalDue;


}












