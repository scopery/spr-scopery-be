package com.company.scopery.modules.quality.testrun.infrastructure.persistence;
import com.company.scopery.modules.quality.testrun.domain.model.*; import com.company.scopery.modules.quality.testrun.infrastructure.mapper.TestCaseResultPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaTestCaseResultRepository implements TestCaseResultRepository {
    private final SpringDataTestCaseResultJpaRepository springData; private final TestCaseResultPersistenceMapper mapper;
    public JpaTestCaseResultRepository(SpringDataTestCaseResultJpaRepository springData, TestCaseResultPersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public TestCaseResult save(TestCaseResult e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestCaseResult> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<TestCaseResult> findByTestRunId(UUID testRunId) { return springData.findByTestRunIdOrderByCreatedAtAsc(testRunId).stream().map(mapper::toDomain).toList(); }
}
