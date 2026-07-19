package com.company.scopery.modules.trust.sensitiveaccesslog.domain.model;
import java.util.List; import java.util.UUID;
public interface SensitiveAccessLogRepository {
    SensitiveAccessLog save(SensitiveAccessLog log);
    List<SensitiveAccessLog> findByWorkspaceId(UUID workspaceId);
}
