package com.company.scopery.modules.collaboration.minutes.infrastructure.persistence;
import com.company.scopery.modules.collaboration.minutes.domain.model.*;
import com.company.scopery.modules.collaboration.minutes.infrastructure.mapper.MeetingMinutesPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingMinutesRepository implements MeetingMinutesRepository {
    private final SpringDataMeetingMinutesJpaRepository springData; private final MeetingMinutesPersistenceMapper mapper;
    public JpaMeetingMinutesRepository(SpringDataMeetingMinutesJpaRepository springData, MeetingMinutesPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public MeetingMinutes save(MeetingMinutes m) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(m))); }
    @Override public Optional<MeetingMinutes> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<MeetingMinutes> findByMeetingId(UUID meetingId) { return springData.findByMeetingIdOrderByCreatedAtDesc(meetingId).stream().map(mapper::toDomain).toList(); }
}
