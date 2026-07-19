package com.company.scopery.modules.productivity.workinbox.application.response;
import com.company.scopery.modules.productivity.workinbox.domain.model.WorkInboxItem;
import java.time.Instant; import java.util.UUID;
public record WorkInboxItemResponse(UUID id, String sourceType, UUID sourceId, String actionType, String title, String priority, Instant dueAt, String status) {
    public static WorkInboxItemResponse from(WorkInboxItem i) { return new WorkInboxItemResponse(i.id(), i.sourceType(), i.sourceId(), i.actionType(), i.title(), i.priority(), i.dueAt(), i.status()); }
}
