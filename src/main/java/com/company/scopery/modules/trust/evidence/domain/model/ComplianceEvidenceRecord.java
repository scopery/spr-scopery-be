package com.company.scopery.modules.trust.evidence.domain.model;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record ComplianceEvidenceRecord(UUID id, UUID workspaceId, String evidenceCode, String evidenceType, String title,
        String description, String status, UUID ownerUserId, LocalDate evidenceDate, Instant finalizedAt, UUID finalizedBy,
        int version, Instant createdAt, Instant updatedAt) {
    public static ComplianceEvidenceRecord create(UUID workspaceId, String type, String title, UUID ownerUserId) {
        Instant now = Instant.now();
        return new ComplianceEvidenceRecord(UUID.randomUUID(), workspaceId, null, type, title, null, "DRAFT",
                ownerUserId, LocalDate.now(), null, null, 0, now, now);
    }
    public ComplianceEvidenceRecord finalizeEvidence(UUID actorId) {
        if (!"DRAFT".equals(status)) throw new IllegalStateException("Only DRAFT can finalize");
        return new ComplianceEvidenceRecord(id, workspaceId, evidenceCode, evidenceType, title, description, "FINALIZED",
                ownerUserId, evidenceDate, Instant.now(), actorId, version, createdAt, Instant.now());
    }
    public ComplianceEvidenceRecord withTitle(String newTitle) {
        if ("FINALIZED".equals(status)) throw new IllegalStateException("immutable");
        return new ComplianceEvidenceRecord(id, workspaceId, evidenceCode, evidenceType, newTitle, description, status,
                ownerUserId, evidenceDate, finalizedAt, finalizedBy, version, createdAt, Instant.now());
    }
}
