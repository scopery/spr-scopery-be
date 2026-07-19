package com.company.scopery.modules.scope.evidence.application.response;
import com.company.scopery.modules.scope.evidence.domain.model.AcceptanceEvidence;
import java.time.Instant; import java.util.UUID;
public record AcceptanceEvidenceResponse(UUID id, UUID deliverableId, UUID acceptanceCriteriaId, UUID projectId,
        String evidenceType, String title, String contentText, String linkUrl, String referenceId, Instant createdAt) {
    public static AcceptanceEvidenceResponse from(AcceptanceEvidence e) {
        return new AcceptanceEvidenceResponse(e.id(), e.deliverableId(), e.acceptanceCriteriaId(), e.projectId(),
                e.evidenceType().name(), e.title(), e.contentText(), e.linkUrl(), e.referenceId(), e.createdAt());
    }
}
