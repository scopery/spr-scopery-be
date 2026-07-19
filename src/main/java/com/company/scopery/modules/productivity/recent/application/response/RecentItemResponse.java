package com.company.scopery.modules.productivity.recent.application.response;
import com.company.scopery.modules.productivity.recent.domain.model.RecentItem;
import java.time.Instant; import java.util.UUID;
public record RecentItemResponse(UUID id, String targetType, UUID targetId, String titleSnapshot, Instant viewedAt) {
    public static RecentItemResponse from(RecentItem r) { return new RecentItemResponse(r.id(), r.targetType(), r.targetId(), r.titleSnapshot(), r.viewedAt()); }
}
