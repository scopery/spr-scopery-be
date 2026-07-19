package com.company.scopery.modules.governance.versioning.infrastructure.persistence;
import com.company.scopery.modules.governance.versioning.domain.model.*;
import com.company.scopery.modules.governance.versioning.infrastructure.mapper.VersioningPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaGovernanceSnapshotRepository implements GovernanceSnapshotRepository {
    private final SpringDataGovernanceSnapshotJpaRepository springData; private final VersioningPersistenceMapper mapper;
    public JpaGovernanceSnapshotRepository(SpringDataGovernanceSnapshotJpaRepository springData, VersioningPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public GovernanceSnapshot save(GovernanceSnapshot s) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpa(s))); }
    @Override public Optional<GovernanceSnapshot> findById(UUID id) { return springData.findById(id).map(mapper::toDomain); }
}
