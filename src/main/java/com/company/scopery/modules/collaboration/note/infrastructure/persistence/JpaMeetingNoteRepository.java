package com.company.scopery.modules.collaboration.note.infrastructure.persistence;
import com.company.scopery.modules.collaboration.note.domain.model.*;
import com.company.scopery.modules.collaboration.note.infrastructure.mapper.MeetingNotePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingNoteRepository implements MeetingNoteRepository {
    private final SpringDataMeetingNoteJpaRepository springData; private final MeetingNotePersistenceMapper mapper;
    public JpaMeetingNoteRepository(SpringDataMeetingNoteJpaRepository springData, MeetingNotePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public MeetingNote save(MeetingNote n) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(n))); }
    @Override public Optional<MeetingNote> findByIdAndMeetingId(UUID id, UUID meetingId) { return springData.findByIdAndMeetingId(id, meetingId).map(mapper::toDomain); }
    @Override public List<MeetingNote> findByMeetingId(UUID meetingId) { return springData.findByMeetingIdOrderByCreatedAtAsc(meetingId).stream().map(mapper::toDomain).toList(); }
}
