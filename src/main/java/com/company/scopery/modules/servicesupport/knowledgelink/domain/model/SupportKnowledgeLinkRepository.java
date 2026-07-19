package com.company.scopery.modules.servicesupport.knowledgelink.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportKnowledgeLinkRepository {
    SupportKnowledgeLink save(SupportKnowledgeLink link);
    Optional<SupportKnowledgeLink> findById(UUID id);
    List<SupportKnowledgeLink> findByWorkspaceId(UUID workspaceId);
    List<SupportKnowledgeLink> findBySupportCaseId(UUID supportCaseId);
}
