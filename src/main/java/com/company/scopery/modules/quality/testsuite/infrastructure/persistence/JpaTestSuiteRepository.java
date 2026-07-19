package com.company.scopery.modules.quality.testsuite.infrastructure.persistence;
import com.company.scopery.modules.quality.testsuite.domain.model.*;
import com.company.scopery.modules.quality.testsuite.infrastructure.mapper.TestSuitePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTestSuiteRepository implements TestSuiteRepository {
    private final SpringDataTestSuiteJpaRepository springData;
    private final TestSuitePersistenceMapper mapper;
    public JpaTestSuiteRepository(SpringDataTestSuiteJpaRepository springData, TestSuitePersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public TestSuite save(TestSuite e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestSuite> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<TestSuite> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<TestSuite> findByProjectIdAndTestPlanId(UUID projectId, UUID testPlanId) {
        return springData.findByProjectIdAndTestPlanIdOrderBySortOrderAscCreatedAtDesc(projectId, testPlanId).stream().map(mapper::toDomain).toList();
    }
}
