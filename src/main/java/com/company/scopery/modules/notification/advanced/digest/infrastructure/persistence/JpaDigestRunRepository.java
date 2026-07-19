package com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.digest.domain.model.*;
import com.company.scopery.modules.notification.advanced.digest.infrastructure.mapper.DigestRunPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDigestRunRepository implements DigestRunRepository {
    private final SpringDataDigestRunJpaRepository springData; private final DigestRunPersistenceMapper mapper;
    public JpaDigestRunRepository(SpringDataDigestRunJpaRepository springData, DigestRunPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public DigestRun save(DigestRun r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<DigestRun> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<DigestRun> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<DigestRun> findByWorkspaceIdAndRecipientUserId(UUID workspaceId, UUID recipientUserId) { return springData.findByWorkspaceIdAndRecipientUserIdOrderByCreatedAtDesc(workspaceId, recipientUserId).stream().map(mapper::toDomain).toList(); }
}
