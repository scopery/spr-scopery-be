package com.company.scopery.modules.collaboration.meetingseries.infrastructure.mapper;
import com.company.scopery.modules.collaboration.meetingseries.domain.enums.*;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.MeetingSeries;
import com.company.scopery.modules.collaboration.meetingseries.infrastructure.persistence.MeetingSeriesJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingSeriesPersistenceMapper {
    public MeetingSeries toDomain(MeetingSeriesJpaEntity e) {
        return new MeetingSeries(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getCode(), e.getTitle(), e.getDescription(),
                e.getCadence()==null?null:MeetingSeriesCadence.valueOf(e.getCadence()), e.getOwnerUserId(),
                MeetingSeriesStatus.valueOf(e.getStatus()), e.isClientVisible(), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingSeriesJpaEntity toJpaEntity(MeetingSeries d) {
        MeetingSeriesJpaEntity e = new MeetingSeriesJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setCode(d.code());
        e.setTitle(d.title()); e.setDescription(d.description());
        e.setCadence(d.cadence()==null?null:d.cadence().name()); e.setOwnerUserId(d.ownerUserId());
        e.setStatus(d.status().name()); e.setClientVisible(d.clientVisible());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
