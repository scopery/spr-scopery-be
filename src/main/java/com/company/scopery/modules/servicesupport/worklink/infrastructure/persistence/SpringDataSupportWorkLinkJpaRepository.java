package com.company.scopery.modules.servicesupport.worklink.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportWorkLinkJpaRepository extends JpaRepository<SupportWorkLinkJpaEntity, UUID> {
    List<SupportWorkLinkJpaEntity> findBySupportCaseId(UUID caseId);
    List<SupportWorkLinkJpaEntity> findByWorkspaceId(UUID workspaceId);
}
