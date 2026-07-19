package com.company.scopery.modules.clientportal.invite.infrastructure.persistence;
import com.company.scopery.modules.clientportal.invite.domain.model.*;
import com.company.scopery.modules.clientportal.invite.infrastructure.mapper.ExternalPortalInvitePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaExternalPortalInviteRepository implements ExternalPortalInviteRepository {
    private final SpringDataExternalPortalInviteJpaRepository springData;
    private final ExternalPortalInvitePersistenceMapper mapper;
    public JpaExternalPortalInviteRepository(SpringDataExternalPortalInviteJpaRepository springData, ExternalPortalInvitePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public ExternalPortalInvite save(ExternalPortalInvite e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ExternalPortalInvite> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public Optional<ExternalPortalInvite> findByInviteTokenHash(String inviteTokenHash) {
        return springData.findByInviteTokenHash(inviteTokenHash).map(mapper::toDomain);
    }
    @Override public List<ExternalPortalInvite> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
