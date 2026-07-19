package com.company.scopery.modules.quality.coverage.infrastructure.persistence;
import com.company.scopery.modules.quality.coverage.domain.model.*;
import com.company.scopery.modules.quality.coverage.infrastructure.mapper.TestCaseCoveragePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTestCaseCoverageRepository implements TestCaseCoverageRepository {
    private final SpringDataTestCaseCoverageJpaRepository springData;
    private final TestCaseCoveragePersistenceMapper mapper;
    public JpaTestCaseCoverageRepository(SpringDataTestCaseCoverageJpaRepository springData, TestCaseCoveragePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public TestCaseCoverage save(TestCaseCoverage e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestCaseCoverage> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<TestCaseCoverage> findByProjectIdAndTestCaseId(UUID projectId, UUID testCaseId) {
        return springData.findByProjectIdAndTestCaseIdOrderByCreatedAtDesc(projectId, testCaseId).stream().map(mapper::toDomain).toList();
    }
}
