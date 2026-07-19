package com.company.scopery.modules.servicesupport.effort.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportEffortRecordJpaRepository extends JpaRepository<SupportEffortRecordJpaEntity, UUID> {
    List<SupportEffortRecordJpaEntity> findBySupportCaseId(UUID supportCaseId);
    List<SupportEffortRecordJpaEntity> findByWorkspaceId(UUID workspaceId);
}
