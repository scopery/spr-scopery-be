package com.company.scopery.modules.collaboration.artifactlink.infrastructure.persistence;
import com.company.scopery.modules.collaboration.artifactlink.domain.model.*;
import com.company.scopery.modules.collaboration.artifactlink.infrastructure.mapper.MeetingArtifactLinkPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaMeetingArtifactLinkRepository implements MeetingArtifactLinkRepository {
    private final SpringDataMeetingArtifactLinkJpaRepository springData; private final MeetingArtifactLinkPersistenceMapper mapper;
    public JpaMeetingArtifactLinkRepository(SpringDataMeetingArtifactLinkJpaRepository springData, MeetingArtifactLinkPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public MeetingArtifactLink save(MeetingArtifactLink link) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(link))); }
    @Override public Optional<MeetingArtifactLink> findByIdAndMeetingId(UUID id, UUID meetingId) { return springData.findByIdAndMeetingId(id, meetingId).map(mapper::toDomain); }
    @Override public List<MeetingArtifactLink> findByMeetingId(UUID meetingId) { return springData.findByMeetingIdAndArchivedAtIsNullOrderByCreatedAtAsc(meetingId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsActive(UUID meetingId, String targetType, UUID targetId) { return springData.existsActive(meetingId, targetType, targetId); }
}
