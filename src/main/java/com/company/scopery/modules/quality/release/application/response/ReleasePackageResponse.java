package com.company.scopery.modules.quality.release.application.response;
import com.company.scopery.modules.quality.release.domain.model.ReleasePackage; import java.time.Instant; import java.util.UUID;
public record ReleasePackageResponse(UUID id, UUID projectId, String code, String versionLabel, String name, String releaseType, String status, String readinessStatus, Instant createdAt) {
    public static ReleasePackageResponse from(ReleasePackage e) {
        return new ReleasePackageResponse(e.id(), e.projectId(), e.code(), e.versionLabel(), e.name(), e.releaseType().name(), e.status().name(),
                e.readinessStatus()==null?null:e.readinessStatus().name(), e.createdAt());
    }
}
