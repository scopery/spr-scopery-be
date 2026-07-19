package com.company.scopery.modules.quality.release.domain.model;
import com.company.scopery.modules.quality.release.domain.enums.ReadinessCheckStatus;
import java.time.Instant; import java.util.UUID;
public record ReleaseReadinessCheck(UUID id, UUID projectId, UUID releasePackageId, String checkCode, String checkName,
        ReadinessCheckStatus status, String details, boolean blocking, String overrideReason, Instant overriddenAt,
        UUID overriddenBy, int version, Instant createdAt, Instant updatedAt) {
    public static ReleaseReadinessCheck create(UUID projectId, UUID releasePackageId, String code, String name,
                                               ReadinessCheckStatus status, String details, boolean blocking) {
        Instant now = Instant.now();
        return new ReleaseReadinessCheck(UUID.randomUUID(), projectId, releasePackageId, code, name, status, details, blocking, null, null, null, 0, now, now);
    }
}
