package com.company.scopery.modules.raid.decision.domain.model;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionCategory;
import com.company.scopery.modules.raid.decision.domain.enums.DecisionStatus;
import java.time.Instant; import java.util.UUID;
public record DecisionRecord(UUID id, UUID projectId, UUID workspaceId, String code, String title, DecisionCategory category,
        DecisionStatus status, String rationale, String outcome, Instant decidedAt, UUID decidedBy,
        UUID supersededByDecisionId, UUID linkedChangeRequestId, int version, Instant createdAt, Instant updatedAt) {
    public static DecisionRecord create(UUID projectId, UUID workspaceId, String code, String title, DecisionCategory category, String rationale) {
        Instant now = Instant.now();
        return new DecisionRecord(UUID.randomUUID(), projectId, workspaceId, code, title, category, DecisionStatus.PROPOSED,
                rationale, null, null, null, null, null, 0, now, now);
    }
    public DecisionRecord decide(UUID actorId, String outcome) {
        return new DecisionRecord(id, projectId, workspaceId, code, title, category, DecisionStatus.DECIDED, rationale,
                outcome, Instant.now(), actorId, supersededByDecisionId, linkedChangeRequestId, version, createdAt, Instant.now());
    }
    public DecisionRecord reject(UUID actorId, String reason) {
        return new DecisionRecord(id, projectId, workspaceId, code, title, category, DecisionStatus.REJECTED, rationale,
                reason, Instant.now(), actorId, supersededByDecisionId, linkedChangeRequestId, version, createdAt, Instant.now());
    }
    public DecisionRecord supersede(UUID replacementId) {
        return new DecisionRecord(id, projectId, workspaceId, code, title, category, DecisionStatus.SUPERSEDED, rationale,
                outcome, decidedAt, decidedBy, replacementId, linkedChangeRequestId, version, createdAt, Instant.now());
    }
    public DecisionRecord archive() {
        return new DecisionRecord(id, projectId, workspaceId, code, title, category, DecisionStatus.ARCHIVED, rationale,
                outcome, decidedAt, decidedBy, supersededByDecisionId, linkedChangeRequestId, version, createdAt, Instant.now());
    }
    public DecisionRecord update(String title, String rationale) {
        return new DecisionRecord(id, projectId, workspaceId, code, title, category, status, rationale, outcome,
                decidedAt, decidedBy, supersededByDecisionId, linkedChangeRequestId, version, createdAt, Instant.now());
    }
}
