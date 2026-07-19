package com.company.scopery.modules.scope.evidence.domain.model;
import com.company.scopery.modules.scope.evidence.domain.enums.EvidenceType;
import java.time.Instant; import java.util.UUID;
public record AcceptanceEvidence(UUID id, UUID deliverableId, UUID acceptanceCriteriaId, UUID projectId,
        EvidenceType evidenceType, String title, String contentText, String linkUrl, String referenceId,
        int version, Instant createdAt) {
    public static AcceptanceEvidence create(UUID deliverableId, UUID acceptanceCriteriaId, UUID projectId,
                                            EvidenceType evidenceType, String title, String contentText,
                                            String linkUrl, String referenceId) {
        return new AcceptanceEvidence(UUID.randomUUID(), deliverableId, acceptanceCriteriaId, projectId, evidenceType,
                title, contentText, linkUrl, referenceId, 0, Instant.now());
    }
}
