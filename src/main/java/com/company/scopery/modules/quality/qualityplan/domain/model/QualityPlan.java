package com.company.scopery.modules.quality.qualityplan.domain.model;
import com.company.scopery.modules.quality.qualityplan.domain.enums.QualityPlanStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record QualityPlan(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID sourceBaselineId,
        String code,
        String name,
        String description,
        QualityPlanStatus status,
        boolean currentFlag,
        String qualityObjectives,
        String testStrategy,
        String entryCriteria,
        String exitCriteria,
        String defectPolicyJson,
        String releaseReadinessPolicyJson,
        Instant approvedAt,
        UUID approvedBy,
        Instant archivedAt,
        UUID archivedBy,
        String traceId,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static QualityPlan create(UUID projectId, UUID workspaceId, String code, String name, String description,
            String qualityObjectives, String testStrategy, String entryCriteria, String exitCriteria) {
        Instant now = Instant.now();
        return new QualityPlan(UUID.randomUUID(), projectId, workspaceId, null, code, name, description,
                QualityPlanStatus.DRAFT, false, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                null, null, null, null, null, null, null, 0, now, now);
    }
    public QualityPlan approve(UUID actorId) {
        if (status != QualityPlanStatus.DRAFT && status != QualityPlanStatus.READY)
            throw new IllegalStateException("Cannot approve from " + status);
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                QualityPlanStatus.APPROVED, currentFlag, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, Instant.now(), actorId, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan markCurrent() {
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                QualityPlanStatus.CURRENT, true, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan clearCurrent() {
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                status == QualityPlanStatus.CURRENT ? QualityPlanStatus.APPROVED : status, false,
                qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan archive(UUID actorId) {
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description,
                QualityPlanStatus.ARCHIVED, false, qualityObjectives, testStrategy, entryCriteria, exitCriteria,
                defectPolicyJson, releaseReadinessPolicyJson, approvedAt, approvedBy, Instant.now(), actorId, traceId, version, createdAt, Instant.now());
    }
    public QualityPlan update(String name, String description, String qualityObjectives, String testStrategy,
                              String entryCriteria, String exitCriteria) {
        if (status != QualityPlanStatus.DRAFT && status != QualityPlanStatus.READY)
            throw new IllegalStateException("Immutable");
        return new QualityPlan(id, projectId, workspaceId, sourceBaselineId, code, name, description, status, currentFlag,
                qualityObjectives, testStrategy, entryCriteria, exitCriteria, defectPolicyJson, releaseReadinessPolicyJson,
                approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
}
