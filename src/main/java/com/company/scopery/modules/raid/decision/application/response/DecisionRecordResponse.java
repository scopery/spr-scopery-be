package com.company.scopery.modules.raid.decision.application.response;
import com.company.scopery.modules.raid.decision.domain.model.DecisionRecord;
import java.time.Instant; import java.util.UUID;
public record DecisionRecordResponse(UUID id, UUID projectId, String code, String title, String category, String status,
        String rationale, String outcome, Instant decidedAt, UUID decidedBy, Instant createdAt) {
    public static DecisionRecordResponse from(DecisionRecord d) {
        return new DecisionRecordResponse(d.id(), d.projectId(), d.code(), d.title(), d.category().name(), d.status().name(),
                d.rationale(), d.outcome(), d.decidedAt(), d.decidedBy(), d.createdAt());
    }
}
