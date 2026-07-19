package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.UUID;
public interface SpringDataSlaBreachJpaRepository extends JpaRepository<SlaBreachJpaEntity, UUID> { List<SlaBreachJpaEntity> findByWorkspaceId(UUID workspaceId); }
