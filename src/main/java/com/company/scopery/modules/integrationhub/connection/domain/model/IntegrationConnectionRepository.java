package com.company.scopery.modules.integrationhub.connection.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface IntegrationConnectionRepository {
    IntegrationConnection save(IntegrationConnection c);
    Optional<IntegrationConnection> findById(UUID id);
    List<IntegrationConnection> findByWorkspaceId(UUID workspaceId);
    List<IntegrationConnection> findByStatus(String status);
}
