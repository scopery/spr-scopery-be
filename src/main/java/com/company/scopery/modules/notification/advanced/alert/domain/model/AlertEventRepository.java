package com.company.scopery.modules.notification.advanced.alert.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface AlertEventRepository {
    AlertEvent save(AlertEvent e);
    Optional<AlertEvent> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<AlertEvent> findByWorkspaceId(UUID workspaceId);
}
