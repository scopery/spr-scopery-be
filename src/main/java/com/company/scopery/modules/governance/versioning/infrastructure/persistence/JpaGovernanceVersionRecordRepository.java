package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import com.company.scopery.modules.governance.versioning.infrastructure.mapper.VersioningPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaGovernanceVersionRecordRepository implements GovernanceVersionRecordRepository {
    private final SpringDataGovernanceVersionRecordJpaRepository springData; private final VersioningPersistenceMapper mapper;
    public JpaGovernanceVersionRecordRepository(SpringDataGovernanceVersionRecordJpaRepository springData, VersioningPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public GovernanceVersionRecord save(GovernanceVersionRecord v) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(v))); }
    @Override public Optional<GovernanceVersionRecord> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
    @Override public List<GovernanceVersionRecord> findByTarget(String objectTypeCode, UUID targetId) {
        return springData.findByObjectTypeCodeAndTargetIdOrderByVersionNumberDesc(objectTypeCode, targetId).stream().map(mapper::toDomain).toList();
    }
    @Override public Optional<GovernanceVersionRecord> findCurrent(String objectTypeCode, UUID targetId) {
        return springData.findByObjectTypeCodeAndTargetIdAndCurrentFlagTrue(objectTypeCode, targetId).map(mapper::toDomain);
    }
    @Override public List<GovernanceVersionRecord> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
