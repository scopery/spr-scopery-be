package com.company.scopery.modules.quality.release.domain.model;
import com.company.scopery.modules.quality.release.domain.enums.*;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record ReleasePackage(UUID id, UUID projectId, UUID workspaceId, String code, String versionLabel, String name,
        String description, ReleaseType releaseType, ReleasePackageStatus status, LocalDate plannedReleaseDate,
        LocalDate actualReleaseDate, ReadinessStatus readinessStatus, String readinessSummaryJson, String releaseNotes,
        Instant approvedAt, UUID approvedBy, Instant releasedAt, UUID releasedBy, Instant archivedAt, UUID archivedBy,
        String traceId, int version, Instant createdAt, Instant updatedAt) {
    public static ReleasePackage create(UUID projectId, UUID workspaceId, String code, String versionLabel, String name,
                                        String description, ReleaseType type, LocalDate planned) {
        Instant now = Instant.now();
        return new ReleasePackage(UUID.randomUUID(), projectId, workspaceId, code, versionLabel, name, description, type,
                ReleasePackageStatus.DRAFT, planned, null, ReadinessStatus.NOT_CHECKED, null, null, null, null, null, null, null, null, null, 0, now, now);
    }
    public ReleasePackage withReadiness(ReadinessStatus rs, String summaryJson) {
        return new ReleasePackage(id, projectId, workspaceId, code, versionLabel, name, description, releaseType, status,
                plannedReleaseDate, actualReleaseDate, rs, summaryJson, releaseNotes, approvedAt, approvedBy, releasedAt, releasedBy,
                archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public ReleasePackage markReady() {
        if (readinessStatus != ReadinessStatus.READY && readinessStatus != ReadinessStatus.OVERRIDE)
            throw new IllegalStateException("not ready");
        return withStatus(ReleasePackageStatus.READY_FOR_RELEASE);
    }
    public ReleasePackage markReleased(UUID actorId) {
        if (status != ReleasePackageStatus.READY_FOR_RELEASE) throw new IllegalStateException("must be ready");
        return new ReleasePackage(id, projectId, workspaceId, code, versionLabel, name, description, releaseType,
                ReleasePackageStatus.RELEASED, plannedReleaseDate, LocalDate.now(), readinessStatus, readinessSummaryJson, releaseNotes,
                approvedAt, approvedBy, Instant.now(), actorId, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public ReleasePackage markRolledBack() { return withStatus(ReleasePackageStatus.ROLLED_BACK); }
    public ReleasePackage approve(UUID actorId) {
        return new ReleasePackage(id, projectId, workspaceId, code, versionLabel, name, description, releaseType, status,
                plannedReleaseDate, actualReleaseDate, readinessStatus, readinessSummaryJson, releaseNotes,
                Instant.now(), actorId, releasedAt, releasedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public ReleasePackage archive(UUID actorId) {
        return new ReleasePackage(id, projectId, workspaceId, code, versionLabel, name, description, releaseType,
                ReleasePackageStatus.ARCHIVED, plannedReleaseDate, actualReleaseDate, readinessStatus, readinessSummaryJson, releaseNotes,
                approvedAt, approvedBy, releasedAt, releasedBy, Instant.now(), actorId, traceId, version, createdAt, Instant.now());
    }
    private ReleasePackage withStatus(ReleasePackageStatus s) {
        return new ReleasePackage(id, projectId, workspaceId, code, versionLabel, name, description, releaseType, s,
                plannedReleaseDate, actualReleaseDate, readinessStatus, readinessSummaryJson, releaseNotes,
                approvedAt, approvedBy, releasedAt, releasedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
}
