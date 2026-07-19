package com.company.scopery.modules.collaboration.meetingseries.infrastructure.persistence;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.*;
import com.company.scopery.modules.collaboration.meetingseries.infrastructure.mapper.MeetingSeriesPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingSeriesRepository implements MeetingSeriesRepository {
    private final SpringDataMeetingSeriesJpaRepository springData;
    private final MeetingSeriesPersistenceMapper mapper;
    public JpaMeetingSeriesRepository(SpringDataMeetingSeriesJpaRepository springData, MeetingSeriesPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public MeetingSeries save(MeetingSeries s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(s))); }
    @Override public Optional<MeetingSeries> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<MeetingSeries> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
