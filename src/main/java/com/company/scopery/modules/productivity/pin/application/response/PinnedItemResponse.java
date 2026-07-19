package com.company.scopery.modules.productivity.pin.application.response;
import com.company.scopery.modules.productivity.pin.domain.model.PinnedItem;
import java.util.UUID;
public record PinnedItemResponse(UUID id, String scope, String targetType, UUID targetId, int sortOrder) {
    public static PinnedItemResponse from(PinnedItem p) { return new PinnedItemResponse(p.id(), p.scope(), p.targetType(), p.targetId(), p.sortOrder()); }
}
