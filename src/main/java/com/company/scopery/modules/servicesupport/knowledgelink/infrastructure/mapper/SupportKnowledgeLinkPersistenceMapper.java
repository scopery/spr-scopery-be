package com.company.scopery.modules.servicesupport.knowledgelink.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.knowledgelink.domain.model.SupportKnowledgeLink;
import com.company.scopery.modules.servicesupport.knowledgelink.infrastructure.persistence.SupportKnowledgeLinkJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportKnowledgeLinkPersistenceMapper {
    public SupportKnowledgeLinkJpaEntity toJpa(SupportKnowledgeLink d) {
        var e = new SupportKnowledgeLinkJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId());
        e.setProblemId(d.problemId()); e.setIncidentId(d.incidentId()); e.setDocumentId(d.documentId());
        e.setDocumentVersionId(d.documentVersionId()); e.setLinkType(d.linkType()); e.setClientVisible(d.clientVisible());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportKnowledgeLink toDomain(SupportKnowledgeLinkJpaEntity e) {
        return new SupportKnowledgeLink(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getProblemId(),
                e.getIncidentId(), e.getDocumentId(), e.getDocumentVersionId(), e.getLinkType(), e.isClientVisible(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
