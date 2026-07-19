package com.company.scopery.modules.scope.deliverable.domain.model;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableStatus;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableType;
import java.time.Instant; import java.util.EnumSet; import java.util.Set; import java.util.UUID;
public record Deliverable(UUID id, UUID projectId, UUID workspaceId, UUID projectPhaseId, UUID scopeItemId,
        DeliverableType type, String code, String title, String description, boolean acceptanceRequired,
        DeliverableStatus status, Instant acceptedAt, UUID acceptedBy, Instant rejectedAt, UUID rejectedBy,
        String rejectionReason, Instant reopenedAt, UUID reopenedBy, String reopenReason,
        Instant archivedAt, UUID archivedBy, int version, Instant createdAt, Instant updatedAt) {
    private static final Set<DeliverableStatus> EDITABLE = EnumSet.of(DeliverableStatus.DRAFT, DeliverableStatus.PLANNED);

    public static Deliverable create(UUID projectId, UUID workspaceId, DeliverableType type, String code, String title,
                                     String description, boolean acceptanceRequired) {
        Instant now = Instant.now();
        return new Deliverable(UUID.randomUUID(), projectId, workspaceId, null, null, type, code, title, description,
                acceptanceRequired, DeliverableStatus.DRAFT, null, null, null, null, null, null, null, null, null, null, 0, now, now);
    }
    public boolean isEditable() { return EDITABLE.contains(status); }
    public Deliverable update(DeliverableType type, String title, String description) {
        if (!isEditable()) throw new IllegalStateException("Deliverable not editable in status " + status);
        return copy(type != null ? type : this.type, title != null ? title : this.title,
                description != null ? description : this.description, status, acceptedAt, acceptedBy,
                rejectedAt, rejectedBy, rejectionReason, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable changeStatus(DeliverableStatus target) {
        if (target == null) throw new IllegalArgumentException("Target status required");
        if (!isAllowedTransition(status, target)) {
            throw new IllegalStateException("Cannot transition from " + status + " to " + target);
        }
        return copy(type, title, description, target, acceptedAt, acceptedBy, rejectedAt, rejectedBy,
                rejectionReason, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable markReadyForReview() {
        if (status != DeliverableStatus.IN_PROGRESS && status != DeliverableStatus.PLANNED) {
            throw new IllegalStateException("Deliverable must be IN_PROGRESS or PLANNED to mark ready for review");
        }
        return copy(type, title, description, DeliverableStatus.READY_FOR_REVIEW, acceptedAt, acceptedBy,
                rejectedAt, rejectedBy, rejectionReason, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable inReview() {
        if (status != DeliverableStatus.READY_FOR_REVIEW && status != DeliverableStatus.IN_PROGRESS) {
            throw new IllegalStateException("Deliverable must be READY_FOR_REVIEW or IN_PROGRESS to enter review");
        }
        return copy(type, title, description, DeliverableStatus.IN_REVIEW, acceptedAt, acceptedBy,
                rejectedAt, rejectedBy, rejectionReason, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable completeReviewApproved() {
        if (status != DeliverableStatus.IN_REVIEW) throw new IllegalStateException("Deliverable must be IN_REVIEW");
        return copy(type, title, description, DeliverableStatus.READY_FOR_REVIEW, acceptedAt, acceptedBy,
                rejectedAt, rejectedBy, rejectionReason, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable requestRework() {
        return copy(type, title, description, DeliverableStatus.REWORK_REQUIRED, null, null,
                null, null, null, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable accept(UUID actorId) {
        return copy(type, title, description, DeliverableStatus.ACCEPTED, Instant.now(), actorId,
                null, null, null, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable reject(UUID actorId, String reason) {
        return copy(type, title, description, DeliverableStatus.REJECTED, null, null,
                Instant.now(), actorId, reason, reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy);
    }
    public Deliverable reopen(UUID actorId, String reason) {
        if (status != DeliverableStatus.ACCEPTED) throw new IllegalStateException("Only accepted deliverables can be reopened");
        return copy(type, title, description, DeliverableStatus.REWORK_REQUIRED, null, null,
                null, null, null, Instant.now(), actorId, reason, archivedAt, archivedBy);
    }
    public Deliverable archive(UUID actorId) {
        if (status == DeliverableStatus.ARCHIVED) throw new IllegalStateException("Deliverable already archived");
        return copy(type, title, description, DeliverableStatus.ARCHIVED, acceptedAt, acceptedBy,
                rejectedAt, rejectedBy, rejectionReason, reopenedAt, reopenedBy, reopenReason, Instant.now(), actorId);
    }
    private static boolean isAllowedTransition(DeliverableStatus from, DeliverableStatus to) {
        return switch (from) {
            case DRAFT -> to == DeliverableStatus.PLANNED;
            case PLANNED -> to == DeliverableStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == DeliverableStatus.READY_FOR_REVIEW;
            case REWORK_REQUIRED -> to == DeliverableStatus.IN_PROGRESS;
            default -> false;
        };
    }
    private Deliverable copy(DeliverableType type, String title, String description, DeliverableStatus status,
                             Instant acceptedAt, UUID acceptedBy, Instant rejectedAt, UUID rejectedBy,
                             String rejectionReason, Instant reopenedAt, UUID reopenedBy, String reopenReason,
                             Instant archivedAt, UUID archivedBy) {
        return new Deliverable(id, projectId, workspaceId, projectPhaseId, scopeItemId, type, code, title, description,
                acceptanceRequired, status, acceptedAt, acceptedBy, rejectedAt, rejectedBy, rejectionReason,
                reopenedAt, reopenedBy, reopenReason, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
}
