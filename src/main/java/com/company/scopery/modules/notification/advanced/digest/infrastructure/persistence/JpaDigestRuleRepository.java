package com.company.scopery.modules.notification.advanced.digest.infrastructure.persistence;
import com.company.scopery.modules.notification.advanced.digest.domain.model.*;
import com.company.scopery.modules.notification.advanced.digest.infrastructure.mapper.DigestRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaDigestRuleRepository implements DigestRuleRepository {
    private final SpringDataDigestRuleJpaRepository springData; private final DigestRulePersistenceMapper mapper;
    public JpaDigestRuleRepository(SpringDataDigestRuleJpaRepository springData, DigestRulePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public DigestRule save(DigestRule r) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(r))); }
    @Override public Optional<DigestRule> findByIdAndWorkspaceId(UUID id, UUID workspaceId) { return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain); }
    @Override public List<DigestRule> findByWorkspaceId(UUID workspaceId) { return springData.findByWorkspaceIdOrderByCodeAsc(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<DigestRule> findAllActive() { return springData.findByStatus("ACTIVE").stream().map(mapper::toDomain).toList(); }
}
