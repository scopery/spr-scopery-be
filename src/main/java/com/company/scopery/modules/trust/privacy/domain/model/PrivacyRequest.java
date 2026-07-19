package com.company.scopery.modules.trust.privacy.domain.model;
import java.time.Instant; import java.util.UUID;
public record PrivacyRequest(UUID id, UUID workspaceId, String requestCode, String requestType, String status,
        String subjectReference, UUID assignedOwnerUserId, String resolutionSummary, String rejectionReason,
        Instant completedAt, int version, Instant createdAt, Instant updatedAt) {
    public static PrivacyRequest submit(UUID workspaceId, String type, String subjectReference, UUID ownerId) {
        Instant now = Instant.now();
        return new PrivacyRequest(UUID.randomUUID(), workspaceId, "PR-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(),
                type, "SUBMITTED", subjectReference, ownerId, null, null, null, 0, now, now);
    }
    public PrivacyRequest triage() { return transition("SUBMITTED","TRIAGED", null, null); }
    public PrivacyRequest markInReview() { return transition("TRIAGED","IN_REVIEW", null, null); }
    public PrivacyRequest complete(String summary) {
        if (summary == null || summary.isBlank()) throw new IllegalArgumentException("resolution summary required");
        var t = transitionAnyTo("COMPLETED");
        return new PrivacyRequest(t.id(), t.workspaceId(), t.requestCode(), t.requestType(), "COMPLETED", t.subjectReference(),
                t.assignedOwnerUserId(), summary, null, Instant.now(), t.version(), t.createdAt(), Instant.now());
    }
    public PrivacyRequest reject(String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("rejection reason required");
        return new PrivacyRequest(id, workspaceId, requestCode, requestType, "REJECTED", subjectReference,
                assignedOwnerUserId, null, reason, Instant.now(), version, createdAt, Instant.now());
    }
    public PrivacyRequest cancel() {
        if ("COMPLETED".equals(status) || "REJECTED".equals(status) || "CANCELLED".equals(status))
            throw new IllegalStateException("Cannot cancel in current status");
        return transitionAnyTo("CANCELLED");
    }
    private PrivacyRequest transition(String from, String to, String summary, String rejection) {
        if (!from.equals(status)) throw new IllegalStateException("Invalid status");
        return new PrivacyRequest(id, workspaceId, requestCode, requestType, to, subjectReference, assignedOwnerUserId,
                summary, rejection, completedAt, version, createdAt, Instant.now());
    }
    private PrivacyRequest transitionAnyTo(String to) {
        return new PrivacyRequest(id, workspaceId, requestCode, requestType, to, subjectReference, assignedOwnerUserId,
                resolutionSummary, rejectionReason, completedAt, version, createdAt, Instant.now());
    }
}
