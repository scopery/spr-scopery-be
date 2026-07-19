package com.company.scopery.modules.notification.advanced.alert.domain.model;
import java.util.*; import java.util.UUID;
public interface AlertRuleRepository {
    AlertRule save(AlertRule r);
    Optional<AlertRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<AlertRule> findByWorkspaceId(UUID workspaceId);
    List<AlertRule> findAllActive();
}
