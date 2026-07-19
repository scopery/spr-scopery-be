package com.company.scopery.modules.servicesupport.supportcase.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportCaseJpaRepository extends JpaRepository<SupportCaseJpaEntity, UUID> {
    List<SupportCaseJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<SupportCaseJpaEntity> findByProjectId(UUID projectId);
}
