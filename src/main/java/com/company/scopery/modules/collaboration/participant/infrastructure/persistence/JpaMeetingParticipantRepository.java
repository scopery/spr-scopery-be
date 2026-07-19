package com.company.scopery.modules.collaboration.participant.infrastructure.persistence;
import com.company.scopery.modules.collaboration.participant.domain.model.*;
import com.company.scopery.modules.collaboration.participant.infrastructure.mapper.MeetingParticipantPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingParticipantRepository implements MeetingParticipantRepository {
    private final SpringDataMeetingParticipantJpaRepository springData;
    private final MeetingParticipantPersistenceMapper mapper;
    public JpaMeetingParticipantRepository(SpringDataMeetingParticipantJpaRepository springData, MeetingParticipantPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public MeetingParticipant save(MeetingParticipant p) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<MeetingParticipant> findByIdAndMeetingId(UUID id, UUID meetingId) {
        return springData.findByIdAndMeetingId(id, meetingId).map(mapper::toDomain);
    }
    @Override public List<MeetingParticipant> findByMeetingId(UUID meetingId) {
        return springData.findByMeetingIdOrderByCreatedAtAsc(meetingId).stream().map(mapper::toDomain).toList();
    }
    @Override public void delete(MeetingParticipant p) { springData.deleteById(p.id()); }
}
