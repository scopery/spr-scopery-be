package com.company.scopery.modules.scope.criteria.domain.model;
import com.company.scopery.modules.scope.criteria.domain.enums.AcceptanceCriteriaStatus;
import java.time.Instant; import java.util.UUID;
public record AcceptanceCriteria(UUID id, UUID deliverableId, UUID projectId, String type, String title, String description,
        boolean mandatory, AcceptanceCriteriaStatus status, String waiveReason, UUID waivedBy, Instant waivedAt,
        int version, Instant createdAt, Instant updatedAt) {
    public static AcceptanceCriteria create(UUID deliverableId, UUID projectId, String type, String title, String description, boolean mandatory) {
        Instant now = Instant.now();
        return new AcceptanceCriteria(UUID.randomUUID(), deliverableId, projectId, type, title, description, mandatory,
                AcceptanceCriteriaStatus.OPEN, null, null, null, 0, now, now);
    }
    public AcceptanceCriteria satisfy() {
        return new AcceptanceCriteria(id, deliverableId, projectId, type, title, description, mandatory,
                AcceptanceCriteriaStatus.SATISFIED, waiveReason, waivedBy, waivedAt, version, createdAt, Instant.now());
    }
    public AcceptanceCriteria waive(UUID actorId, String reason) {
        return new AcceptanceCriteria(id, deliverableId, projectId, type, title, description, mandatory,
                AcceptanceCriteriaStatus.WAIVED, reason, actorId, Instant.now(), version, createdAt, Instant.now());
    }
    public boolean isMet() { return status == AcceptanceCriteriaStatus.SATISFIED || status == AcceptanceCriteriaStatus.WAIVED || !mandatory; }
}
