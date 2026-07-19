package com.company.scopery.modules.projectfinance.customcost.domain.model;

import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostCategory;
import com.company.scopery.modules.projectfinance.customcost.domain.enums.CustomCostStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectCustomCost(
        UUID id,
        UUID financeScenarioId,
        UUID projectId,
        UUID projectPhaseId,
        CustomCostCategory category,
        String name,
        String description,
        BigDecimal amount,
        String currencyCode,
        LocalDate costDate,
        CustomCostStatus status,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectCustomCost create(
            UUID financeScenarioId,
            UUID projectId,
            UUID projectPhaseId,
            CustomCostCategory category,
            String name,
            String description,
            BigDecimal amount,
            String currencyCode,
            LocalDate costDate) {
        return new ProjectCustomCost(
                UUID.randomUUID(), financeScenarioId, projectId, projectPhaseId, category, name,
                description, amount, currencyCode, costDate, CustomCostStatus.ACTIVE,
                null, null, 0, null, null);
    }

    public ProjectCustomCost update(
            UUID projectPhaseId,
            CustomCostCategory category,
            String name,
            String description,
            BigDecimal amount,
            String currencyCode,
            LocalDate costDate) {
        return new ProjectCustomCost(
                id, financeScenarioId, projectId, projectPhaseId, category, name, description,
                amount, currencyCode, costDate, status, archivedAt, archivedBy, version,
                createdAt, updatedAt);
    }

    public ProjectCustomCost archive(UUID actorId) {
        return new ProjectCustomCost(
                id, financeScenarioId, projectId, projectPhaseId, category, name, description,
                amount, currencyCode, costDate, CustomCostStatus.ARCHIVED, Instant.now(), actorId,
                version, createdAt, updatedAt);
    }

    public boolean isActive() {
        return status == CustomCostStatus.ACTIVE;
    }
}
