package com.company.scopery.modules.productivity.favorite.application.response;
import com.company.scopery.modules.productivity.favorite.domain.model.FavoriteItem;
import java.util.UUID;
public record FavoriteItemResponse(UUID id, String targetType, UUID targetId, String labelOverride) {
    public static FavoriteItemResponse from(FavoriteItem f) { return new FavoriteItemResponse(f.id(), f.targetType(), f.targetId(), f.labelOverride()); }
}
