package com.company.scopery.modules.collaboration.note.infrastructure.mapper;
import com.company.scopery.modules.collaboration.note.domain.enums.NoteType;
import com.company.scopery.modules.collaboration.note.domain.model.MeetingNote;
import com.company.scopery.modules.collaboration.note.infrastructure.persistence.MeetingNoteJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class MeetingNotePersistenceMapper {
    public MeetingNote toDomain(MeetingNoteJpaEntity e) {
        return new MeetingNote(e.getId(), e.getWorkspaceId(), e.getProjectId(), e.getMeetingId(), e.getAgendaItemId(),
                NoteType.valueOf(e.getNoteType()), e.getBody(), e.isInternalOnly(), e.isClientVisible(), e.getSourceAiSuggestionId(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public MeetingNoteJpaEntity toJpaEntity(MeetingNote d) {
        MeetingNoteJpaEntity e = new MeetingNoteJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setProjectId(d.projectId()); e.setMeetingId(d.meetingId());
        e.setAgendaItemId(d.agendaItemId()); e.setNoteType(d.noteType().name()); e.setBody(d.body());
        e.setInternalOnly(d.internalOnly()); e.setClientVisible(d.clientVisible()); e.setSourceAiSuggestionId(d.sourceAiSuggestionId());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
