package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSlaTargetJpaRepository extends JpaRepository<SlaTargetJpaEntity, UUID> {
    List<SlaTargetJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<SlaTargetJpaEntity> findBySlaPolicyId(UUID slaPolicyId);
}
