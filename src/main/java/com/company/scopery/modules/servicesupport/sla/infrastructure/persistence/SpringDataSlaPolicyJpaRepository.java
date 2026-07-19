package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.UUID;
public interface SpringDataSlaPolicyJpaRepository extends JpaRepository<SlaPolicyJpaEntity, UUID> { List<SlaPolicyJpaEntity> findByWorkspaceId(UUID workspaceId); }
