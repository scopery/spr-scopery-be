package com.company.scopery.modules.quality.teststep.infrastructure.persistence;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStepRepository;
import com.company.scopery.modules.quality.teststep.infrastructure.mapper.TestCaseStepPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTestCaseStepRepository implements TestCaseStepRepository {
    private final SpringDataTestCaseStepJpaRepository springData;
    private final TestCaseStepPersistenceMapper mapper;
    public JpaTestCaseStepRepository(SpringDataTestCaseStepJpaRepository springData, TestCaseStepPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public TestCaseStep save(TestCaseStep e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestCaseStep> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<TestCaseStep> findByTestCaseIdOrderBySortOrder(UUID testCaseId) {
        return springData.findByTestCaseIdOrderBySortOrderAsc(testCaseId).stream().map(mapper::toDomain).toList();
    }
    @Override public int findMaxSortOrderByTestCaseId(UUID testCaseId) {
        return springData.findMaxSortOrderByTestCaseId(testCaseId);
    }
    @Override public void saveAll(List<TestCaseStep> entities) {
        springData.saveAllAndFlush(entities.stream().map(mapper::toJpaEntity).toList());
    }
    @Override public long countActiveByTestCaseId(UUID testCaseId) {
        return springData.countByTestCaseIdAndArchivedAtIsNull(testCaseId);
    }
}
