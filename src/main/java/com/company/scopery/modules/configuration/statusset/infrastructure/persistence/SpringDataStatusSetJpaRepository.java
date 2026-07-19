package com.company.scopery.modules.configuration.statusset.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataStatusSetJpaRepository extends JpaRepository<StatusSetJpaEntity, UUID> {
    Optional<StatusSetJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<StatusSetJpaEntity> findByWorkspaceIdOrderByNameAsc(UUID workspaceId);
}
