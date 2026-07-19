package com.company.scopery.modules.collaboration.meeting.infrastructure.persistence;
import com.company.scopery.modules.collaboration.meeting.domain.model.Meeting;
import com.company.scopery.modules.collaboration.meeting.domain.model.MeetingRepository;
import com.company.scopery.modules.collaboration.meeting.infrastructure.mapper.MeetingPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingRepository implements MeetingRepository {
    private final SpringDataMeetingJpaRepository springData;
    private final MeetingPersistenceMapper mapper;
    public JpaMeetingRepository(SpringDataMeetingJpaRepository springData, MeetingPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public Meeting save(Meeting m) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(m))); }
    @Override public Optional<Meeting> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<Meeting> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByStartAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
