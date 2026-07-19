package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List; import java.util.UUID;
public interface SpringDataSlaClockJpaRepository extends JpaRepository<SlaClockJpaEntity, UUID> {
    List<SlaClockJpaEntity> findByWorkspaceId(UUID workspaceId); List<SlaClockJpaEntity> findBySupportCaseId(UUID supportCaseId);
    List<SlaClockJpaEntity> findByStatusIn(List<String> statuses);
}
