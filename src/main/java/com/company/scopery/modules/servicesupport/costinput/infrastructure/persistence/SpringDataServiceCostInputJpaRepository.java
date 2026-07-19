package com.company.scopery.modules.servicesupport.costinput.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataServiceCostInputJpaRepository extends JpaRepository<ServiceCostInputJpaEntity, UUID> {
    List<ServiceCostInputJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<ServiceCostInputJpaEntity> findBySupportCaseId(UUID caseId);
}
