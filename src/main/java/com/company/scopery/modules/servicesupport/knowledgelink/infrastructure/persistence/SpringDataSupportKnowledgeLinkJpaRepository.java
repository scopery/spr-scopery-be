package com.company.scopery.modules.servicesupport.knowledgelink.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSupportKnowledgeLinkJpaRepository extends JpaRepository<SupportKnowledgeLinkJpaEntity, UUID> {
    List<SupportKnowledgeLinkJpaEntity> findByWorkspaceId(UUID workspaceId);
    List<SupportKnowledgeLinkJpaEntity> findBySupportCaseId(UUID caseId);
}
