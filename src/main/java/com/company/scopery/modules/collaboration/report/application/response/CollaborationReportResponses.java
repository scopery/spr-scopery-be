package com.company.scopery.modules.collaboration.report.application.response;
import java.util.List; import java.util.UUID;
public final class CollaborationReportResponses {
    private CollaborationReportResponses() {}
    public record MeetingRegisterRow(UUID id, String title, String meetingType, String status, boolean clientVisible) {}
    public record MeetingActionReportRow(UUID id, UUID meetingId, String title, String status, java.time.LocalDate dueDate, boolean overdue) {}
    public record MinutesStatusRow(UUID meetingId, UUID minutesId, String status) {}
    public record CommentActivityRow(UUID threadId, String targetType, UUID targetId, String status) {}
}
