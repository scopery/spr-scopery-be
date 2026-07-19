package com.company.scopery.modules.servicesupport.worklink.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLink;
import com.company.scopery.modules.servicesupport.worklink.infrastructure.persistence.SupportWorkLinkJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportWorkLinkPersistenceMapper {
    public SupportWorkLinkJpaEntity toJpa(SupportWorkLink d) {
        var e = new SupportWorkLinkJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSupportCaseId(d.supportCaseId());
        e.setTargetObjectType(d.targetObjectType()); e.setTargetObjectId(d.targetObjectId());
        e.setLinkType(d.linkType()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportWorkLink toDomain(SupportWorkLinkJpaEntity e) {
        return new SupportWorkLink(e.getId(), e.getWorkspaceId(), e.getSupportCaseId(), e.getTargetObjectType(),
                e.getTargetObjectId(), e.getLinkType(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
