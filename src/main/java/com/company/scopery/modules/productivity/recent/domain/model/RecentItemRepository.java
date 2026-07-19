package com.company.scopery.modules.productivity.recent.domain.model;
import java.util.List; import java.util.UUID;
public interface RecentItemRepository {
    RecentItem save(RecentItem r);
    List<RecentItem> findRecentByUser(UUID userId, int limit);
}
