package com.company.scopery.modules.collaboration.agendaitem.infrastructure.persistence;
import com.company.scopery.modules.collaboration.agendaitem.domain.model.*;
import com.company.scopery.modules.collaboration.agendaitem.infrastructure.mapper.MeetingAgendaItemPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingAgendaItemRepository implements MeetingAgendaItemRepository {
    private final SpringDataMeetingAgendaItemJpaRepository springData; private final MeetingAgendaItemPersistenceMapper mapper;
    public JpaMeetingAgendaItemRepository(SpringDataMeetingAgendaItemJpaRepository springData, MeetingAgendaItemPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public MeetingAgendaItem save(MeetingAgendaItem item) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(item))); }
    @Override public Optional<MeetingAgendaItem> findByIdAndMeetingId(UUID id, UUID meetingId) { return springData.findByIdAndMeetingId(id, meetingId).map(mapper::toDomain); }
    @Override public List<MeetingAgendaItem> findByMeetingId(UUID meetingId) { return springData.findByMeetingIdOrderBySortOrderAsc(meetingId).stream().map(mapper::toDomain).toList(); }
}
