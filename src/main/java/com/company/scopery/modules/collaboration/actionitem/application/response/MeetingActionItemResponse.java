package com.company.scopery.modules.collaboration.actionitem.application.response;
import com.company.scopery.modules.collaboration.actionitem.domain.model.MeetingActionItem;
import java.time.Instant; import java.time.LocalDate; import java.util.UUID;
public record MeetingActionItemResponse(UUID id, UUID meetingId, String title, String description, String ownerTargetType, UUID ownerTargetId,
        LocalDate dueDate, String status, UUID linkedTaskId, Instant completedAt, boolean clientVisible) {
    public static MeetingActionItemResponse from(MeetingActionItem a) {
        return new MeetingActionItemResponse(a.id(), a.meetingId(), a.title(), a.description(), a.ownerTargetType(), a.ownerTargetId(),
                a.dueDate(), a.status().name(), a.linkedTaskId(), a.completedAt(), a.clientVisible());
    }
}
