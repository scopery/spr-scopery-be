package com.company.scopery.modules.servicesupport.requesttype.infrastructure.mapper;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestType;
import com.company.scopery.modules.servicesupport.requesttype.infrastructure.persistence.SupportRequestTypeJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class SupportRequestTypePersistenceMapper {
    public SupportRequestTypeJpaEntity toJpa(SupportRequestType d) {
        var e = new SupportRequestTypeJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setTypeCode(d.typeCode());
        e.setName(d.name()); e.setDescription(d.description()); e.setDefaultPriority(d.defaultPriority());
        e.setPortalVisible(d.portalVisible()); e.setEnabled(d.enabled()); e.setStatus(d.status());
        e.setCreatedAt(d.createdAt());
        return e;
    }
    public SupportRequestType toDomain(SupportRequestTypeJpaEntity e) {
        return new SupportRequestType(e.getId(), e.getWorkspaceId(), e.getTypeCode(), e.getName(),
                e.getDescription(), e.getDefaultPriority(), e.isPortalVisible(), e.isEnabled(), e.getStatus(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
