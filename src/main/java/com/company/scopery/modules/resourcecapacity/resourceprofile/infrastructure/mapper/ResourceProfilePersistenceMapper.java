package com.company.scopery.modules.resourcecapacity.resourceprofile.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceProfileStatus;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.enums.ResourceType;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfile;
import com.company.scopery.modules.resourcecapacity.resourceprofile.infrastructure.persistence.ResourceProfileJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ResourceProfilePersistenceMapper {
    public ResourceProfileJpaEntity toJpaEntity(ResourceProfile d) {
        ResourceProfileJpaEntity e = new ResourceProfileJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setLinkedUserId(d.linkedUserId());
        e.setLinkedWorkspaceMemberId(d.linkedWorkspaceMemberId()); e.setLinkedTeamId(d.linkedTeamId());
        e.setLinkedExternalContactId(d.linkedExternalContactId()); e.setResourceType(d.resourceType().name());
        e.setDisplayName(d.displayName()); e.setPrimaryRoleId(d.primaryRoleId()); e.setDefaultCalendarId(d.defaultCalendarId());
        e.setDefaultRateCardId(d.defaultRateCardId()); e.setTimezone(d.timezone()); e.setStatus(d.status().name());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        e.setCreatedAt(d.createdAt()); return e;
    }
    public ResourceProfile toDomain(ResourceProfileJpaEntity e) {
        return new ResourceProfile(e.getId(), e.getWorkspaceId(), e.getLinkedUserId(), e.getLinkedWorkspaceMemberId(),
                e.getLinkedTeamId(), e.getLinkedExternalContactId(), ResourceType.valueOf(e.getResourceType()),
                e.getDisplayName(), e.getPrimaryRoleId(), e.getDefaultCalendarId(), e.getDefaultRateCardId(),
                e.getTimezone(), ResourceProfileStatus.valueOf(e.getStatus()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
