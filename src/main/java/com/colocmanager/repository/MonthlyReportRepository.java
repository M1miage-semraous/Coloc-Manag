package com.colocmanager.repository;

import com.colocmanager.model.MonthlyReport;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MonthlyReportRepository {

    private List<MonthlyReport> reports = new ArrayList<>();

    public void save(MonthlyReport report) {
        reports.add(report);
    }

    public List<MonthlyReport> findAll() {
        return new ArrayList<>(reports);
    }

    public Optional<MonthlyReport> findById(UUID id) {
        return reports.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public List<MonthlyReport> findByOwner(UUID userId) {
        return reports.stream()
                .filter(r -> r.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<MonthlyReport> findByOwnerAndMonth(UUID userId, int month, int year) {
        return reports.stream()
                .filter(r -> r.getOwner().getId().equals(userId)
                        && r.getMonth() == month
                        && r.getYear() == year)
                .findFirst();
    }

    public List<MonthlyReport> findByMonth(int month, int year) {
        return reports.stream()
                .filter(r -> r.getMonth() == month
                        && r.getYear() == year)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        reports.removeIf(r -> r.getId().equals(id));
    }

    public int count() {
        return reports.size();
    }
}
