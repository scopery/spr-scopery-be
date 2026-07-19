package com.company.scopery.modules.trust.privacy.application.response;
import com.company.scopery.modules.trust.privacy.domain.model.PrivacyExportPackage;
import java.time.Instant; import java.util.UUID;
public record PrivacyExportPackageResponse(UUID id, UUID workspaceId, UUID privacyRequestId,
        UUID dataSubjectIndexId, String status, String packageManifestJson,
        String fileReference, Instant expiresAt, Instant completedAt) {
    public static PrivacyExportPackageResponse from(PrivacyExportPackage p) {
        return new PrivacyExportPackageResponse(p.id(), p.workspaceId(), p.privacyRequestId(),
                p.dataSubjectIndexId(), p.status(), p.packageManifestJson(),
                p.fileReference(), p.expiresAt(), p.completedAt());
    }
}
