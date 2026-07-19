package com.company.scopery.modules.trust.privacy.domain.model;
import java.time.Instant; import java.util.UUID;
public record PrivacyExportPackage(UUID id, UUID workspaceId, UUID privacyRequestId, UUID dataSubjectIndexId, String status,
        String packageManifestJson, String fileReference, Instant expiresAt, Instant completedAt, int version, Instant createdAt) {
    public static PrivacyExportPackage create(UUID workspaceId, UUID privacyRequestId, UUID subjectIndexId, String manifestJson) {
        Instant now = Instant.now();
        return new PrivacyExportPackage(UUID.randomUUID(), workspaceId, privacyRequestId, subjectIndexId, "READY",
                manifestJson == null ? "{\"items\":[]}" : manifestJson, "export://privacy/"+UUID.randomUUID(),
                now.plusSeconds(7*24*3600), now, 0, now);
    }
    public PrivacyExportPackage expire() {
        return new PrivacyExportPackage(id, workspaceId, privacyRequestId, dataSubjectIndexId, "EXPIRED", packageManifestJson,
                fileReference, Instant.now(), completedAt, version, createdAt);
    }
}
