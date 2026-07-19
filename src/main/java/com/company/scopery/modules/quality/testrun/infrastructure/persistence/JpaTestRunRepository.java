package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.modules.quality.testrun.domain.model.*; import com.company.scopery.modules.quality.testrun.infrastructure.mapper.TestRunPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaTestRunRepository implements TestRunRepository {
    private final SpringDataTestRunJpaRepository springData; private final TestRunPersistenceMapper mapper;
    public JpaTestRunRepository(SpringDataTestRunJpaRepository springData, TestRunPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public TestRun save(TestRun e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestRun> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<TestRun> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
}
