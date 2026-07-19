package com.company.scopery.modules.collaboration.artifactlink.infrastructure.mapper;
import com.company.scopery.modules.collaboration.artifactlink.domain.enums.ArtifactLinkType;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.MeetingArtifactLink;
import com.company.scopery.modules.collaboration.artifactlink.infrastructure.persistence.MeetingArtifactLinkJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingArtifactLinkPersistenceMapper {
    public MeetingArtifactLink toDomain(MeetingArtifactLinkJpaEntity e) {
        return new MeetingArtifactLink(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingId(), e.getAgendaItemId(), e.getNoteId(), e.getActionItemId(),
                e.getTargetType(), e.getTargetId(), ArtifactLinkType.valueOf(e.getLinkType()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingArtifactLinkJpaEntity toJpaEntity(MeetingArtifactLink d) {
        MeetingArtifactLinkJpaEntity e = new MeetingArtifactLinkJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setMeetingId(d.meetingId());
        e.setAgendaItemId(d.agendaItemId()); e.setNoteId(d.noteId()); e.setActionItemId(d.actionItemId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setLinkType(d.linkType().name());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
