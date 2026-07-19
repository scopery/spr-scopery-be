package com.company.scopery.modules.quality.testplan.domain.model;
import com.company.scopery.modules.quality.testplan.domain.enums.TestPlanStatus;
import com.company.scopery.modules.quality.testplan.domain.enums.TestLevel;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record TestPlan(UUID id, UUID projectId, UUID workspaceId, UUID qualityPlanId, UUID releasePackageId, String code, String name, String description, TestLevel testLevel, TestPlanStatus status, Instant approvedAt, UUID approvedBy, Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    public static TestPlan create(UUID projectId, UUID workspaceId, UUID qualityPlanId, UUID releasePackageId, String code, String name, String description, TestLevel level) {
        Instant now = Instant.now();
        return new TestPlan(UUID.randomUUID(), projectId, workspaceId, qualityPlanId, releasePackageId, code, name, description, level, TestPlanStatus.DRAFT, null, null, null, null, 0, now, now);
    }
    public TestPlan approve(UUID actorId) {
        return new TestPlan(id, projectId, workspaceId, qualityPlanId, releasePackageId, code, name, description, testLevel, TestPlanStatus.APPROVED, Instant.now(), actorId, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public TestPlan archive(UUID actorId) {
        return new TestPlan(id, projectId, workspaceId, qualityPlanId, releasePackageId, code, name, description, testLevel, TestPlanStatus.ARCHIVED, approvedAt, approvedBy, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
