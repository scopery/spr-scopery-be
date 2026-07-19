package com.company.scopery.modules.servicesupport.supportcase.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import com.company.scopery.modules.servicesupport.supportcase.infrastructure.persistence.SupportCaseJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportCasePersistenceMapper {
    public SupportCaseJpaEntity toJpaEntity(SupportCase d) {
        SupportCaseJpaEntity e = new SupportCaseJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setCaseNumber(d.caseNumber());
        e.setRequestTypeCode(d.requestTypeCode()); e.setSource(d.source()); e.setPriority(d.priority()); e.setStatus(d.status());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setOwnerUserId(d.ownerUserId());
        e.setPortalVisible(d.portalVisible()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public SupportCase toDomain(SupportCaseJpaEntity e) {
        return new SupportCase(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getCaseNumber(), e.getRequestTypeCode(),
                e.getSource(), e.getPriority(), e.getStatus(), e.getTitle(), e.getDescription(), e.getOwnerUserId(),
                e.isPortalVisible(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
