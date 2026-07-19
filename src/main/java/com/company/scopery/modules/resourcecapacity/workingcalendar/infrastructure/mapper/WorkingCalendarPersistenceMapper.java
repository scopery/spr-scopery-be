package com.company.scopery.modules.resourcecapacity.workingcalendar.infrastructure.mapper;

import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.enums.WorkingCalendarStatus;
import com.company.scopery.modules.resourcecapacity.workingcalendar.domain.model.WorkingCalendar;
import com.company.scopery.modules.resourcecapacity.workingcalendar.infrastructure.persistence.WorkingCalendarJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkingCalendarPersistenceMapper {

    public WorkingCalendar toDomain(WorkingCalendarJpaEntity entity) {
        return new WorkingCalendar(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getTimezone(),
                entity.isDefault(),
                WorkingCalendarStatus.valueOf(entity.getStatus()),
                entity.getArchivedAt(),
                entity.getArchivedBy(),
                entity.getVersion() != null ? entity.getVersion() : 0,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public WorkingCalendarJpaEntity toJpaEntity(WorkingCalendar domain) {
        WorkingCalendarJpaEntity entity = new WorkingCalendarJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setTimezone(domain.timezone());
        entity.setDefault(domain.isDefault());
        entity.setStatus(domain.status().name());
        entity.setArchivedAt(domain.archivedAt());
        entity.setArchivedBy(domain.archivedBy());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }
}
