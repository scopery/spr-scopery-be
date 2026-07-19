package com.company.scopery.modules.collaboration.actionitem.infrastructure.persistence;
import com.company.scopery.modules.collaboration.actionitem.domain.model.*;
import com.company.scopery.modules.collaboration.actionitem.infrastructure.mapper.MeetingActionItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingActionItemRepository implements MeetingActionItemRepository {
    private final SpringDataMeetingActionItemJpaRepository springData; private final MeetingActionItemPersistenceMapper mapper;
    public JpaMeetingActionItemRepository(SpringDataMeetingActionItemJpaRepository springData, MeetingActionItemPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public MeetingActionItem save(MeetingActionItem i) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(i))); }
    @Override public Optional<MeetingActionItem> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<MeetingActionItem> findByMeetingId(UUID meetingId) { return springData.findByMeetingIdOrderByCreatedAtAsc(meetingId).stream().map(mapper::toDomain).toList(); }
    @Override public List<MeetingActionItem> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByDueDateAsc(projectId).stream().map(mapper::toDomain).toList(); }
}
