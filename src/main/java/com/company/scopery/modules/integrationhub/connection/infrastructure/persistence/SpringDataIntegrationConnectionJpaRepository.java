package com.company.scopery.modules.integrationhub.connection.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataIntegrationConnectionJpaRepository extends JpaRepository<IntegrationConnectionJpaEntity, UUID> {
    List<IntegrationConnectionJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<IntegrationConnectionJpaEntity> findByStatus(String status);
}
