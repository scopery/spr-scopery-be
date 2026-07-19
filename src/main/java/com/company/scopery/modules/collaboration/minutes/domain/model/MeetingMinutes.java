package com.company.scopery.modules.collaboration.minutes.domain.model;
import com.company.scopery.modules.collaboration.minutes.domain.enums.MinutesStatus;
import java.time.Instant; import java.util.UUID;
public record MeetingMinutes(
        UUID id, UUID workspaceId, UUID projectId, UUID meetingId, MinutesStatus status,
        String summary, String decisionsSummary, String actionsSummary, String clientVisibleSummary,
        UUID documentId, UUID documentVersionId,
        Instant submittedAt, UUID submittedBy, Instant approvedAt, UUID approvedBy,
        Instant rejectedAt, UUID rejectedBy, String rejectionReason, String traceId,
        int version, Instant createdAt, Instant updatedAt
) {
    public static MeetingMinutes create(UUID workspaceId, UUID projectId, UUID meetingId, String summary,
                                        String decisionsSummary, String actionsSummary, String clientVisibleSummary) {
        Instant now = Instant.now();
        return new MeetingMinutes(UUID.randomUUID(), workspaceId, projectId, meetingId, MinutesStatus.DRAFT,
                summary, decisionsSummary, actionsSummary, clientVisibleSummary, null, null,
                null, null, null, null, null, null, null, null, 0, now, now);
    }
    public MeetingMinutes update(String summary, String decisionsSummary, String actionsSummary, String clientVisibleSummary) {
        if (status == MinutesStatus.APPROVED)
            throw new IllegalStateException("Approved minutes are immutable");
        return new MeetingMinutes(id, workspaceId, projectId, meetingId, status, summary, decisionsSummary,
                actionsSummary, clientVisibleSummary, documentId, documentVersionId, submittedAt, submittedBy,
                approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason, traceId, version, createdAt, Instant.now());
    }
    public MeetingMinutes submit(UUID actorId) {
        if (status != MinutesStatus.DRAFT && status != MinutesStatus.REJECTED)
            throw new IllegalStateException("Only DRAFT/REJECTED minutes can be submitted");
        Instant now = Instant.now();
        return new MeetingMinutes(id, workspaceId, projectId, meetingId, MinutesStatus.IN_REVIEW, summary,
                decisionsSummary, actionsSummary, clientVisibleSummary, documentId, documentVersionId,
                now, actorId, approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason, traceId, version, createdAt, now);
    }
    public MeetingMinutes approve(UUID actorId) {
        if (status != MinutesStatus.IN_REVIEW)
            throw new IllegalStateException("Only IN_REVIEW minutes can be approved");
        Instant now = Instant.now();
        return new MeetingMinutes(id, workspaceId, projectId, meetingId, MinutesStatus.APPROVED, summary,
                decisionsSummary, actionsSummary, clientVisibleSummary, documentId, documentVersionId,
                submittedAt, submittedBy, now, actorId, null, null, null, traceId, version, createdAt, now);
    }
    public MeetingMinutes reject(UUID actorId, String reason) {
        if (status != MinutesStatus.IN_REVIEW)
            throw new IllegalStateException("Only IN_REVIEW minutes can be rejected");
        Instant now = Instant.now();
        return new MeetingMinutes(id, workspaceId, projectId, meetingId, MinutesStatus.REJECTED, summary,
                decisionsSummary, actionsSummary, clientVisibleSummary, documentId, documentVersionId,
                submittedAt, submittedBy, approvedAt, approvedBy, now, actorId, reason, traceId, version, createdAt, now);
    }
    public MeetingMinutes attachDocument(UUID documentId, UUID documentVersionId) {
        if (documentId == null) throw new IllegalArgumentException("documentId is required");
        if (this.documentId != null)
            throw new IllegalStateException("Minutes already linked to a document");
        return new MeetingMinutes(id, workspaceId, projectId, meetingId, status, summary, decisionsSummary,
                actionsSummary, clientVisibleSummary, documentId, documentVersionId, submittedAt, submittedBy,
                approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason, traceId, version, createdAt, Instant.now());
    }
}
