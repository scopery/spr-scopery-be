package com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.mapper;

import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.enums.UserCapacityProfileStatus;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.domain.model.UserCapacityProfile;
import com.company.scopery.modules.resourcecapacity.usercapacityprofile.infrastructure.persistence.UserCapacityProfileJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserCapacityProfilePersistenceMapper {

    public UserCapacityProfile toDomain(UserCapacityProfileJpaEntity entity) {
        return new UserCapacityProfile(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getWorkspaceMemberId(),
                entity.getUserId(),
                entity.getWorkingCalendarId(),
                entity.getDefaultDailyHours(),
                entity.getFocusFactor(),
                UserCapacityProfileStatus.valueOf(entity.getCapacityStatus()),
                entity.getEffectiveFrom(),
                entity.getEffectiveTo(),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserCapacityProfileJpaEntity toJpaEntity(UserCapacityProfile domain) {
        UserCapacityProfileJpaEntity entity = new UserCapacityProfileJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setWorkspaceMemberId(domain.workspaceMemberId());
        entity.setUserId(domain.userId());
        entity.setWorkingCalendarId(domain.workingCalendarId());
        entity.setDefaultDailyHours(domain.defaultDailyHours());
        entity.setFocusFactor(domain.focusFactor());
        entity.setCapacityStatus(domain.capacityStatus().name());
        entity.setEffectiveFrom(domain.effectiveFrom());
        entity.setEffectiveTo(domain.effectiveTo());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
