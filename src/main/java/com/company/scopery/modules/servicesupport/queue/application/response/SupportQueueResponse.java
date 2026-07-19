package com.company.scopery.modules.servicesupport.queue.application.response;
import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueue;
import java.time.Instant; import java.util.UUID;
public record SupportQueueResponse(UUID id, UUID workspaceId, String queueCode, String name, String status, Instant createdAt) {
    public static SupportQueueResponse from(SupportQueue q) {
        return new SupportQueueResponse(q.id(), q.workspaceId(), q.queueCode(), q.name(), q.status(), q.createdAt());
    }
}
