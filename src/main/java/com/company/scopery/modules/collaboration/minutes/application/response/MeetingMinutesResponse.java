package com.company.scopery.modules.collaboration.minutes.application.response;
import com.company.scopery.modules.collaboration.minutes.domain.model.MeetingMinutes;
import java.time.Instant; import java.util.UUID;
public record MeetingMinutesResponse(UUID id, UUID meetingId, String status, String summary, String decisionsSummary, String actionsSummary,
        String clientVisibleSummary, UUID documentId, UUID documentVersionId,
        Instant submittedAt, Instant approvedAt, Instant rejectedAt, String rejectionReason, Instant createdAt) {
    public static MeetingMinutesResponse from(MeetingMinutes m) {
        return new MeetingMinutesResponse(m.id(), m.meetingId(), m.status().name(), m.summary(), m.decisionsSummary(), m.actionsSummary(),
                m.clientVisibleSummary(), m.documentId(), m.documentVersionId(),
                m.submittedAt(), m.approvedAt(), m.rejectedAt(), m.rejectionReason(), m.createdAt());
    }
}
