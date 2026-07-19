package com.company.scopery.modules.quality.teststep.infrastructure.persistence;
import com.company.scopery.modules.quality.teststep.domain.model.*;
import com.company.scopery.modules.quality.teststep.infrastructure.mapper.TestStepPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTestStepRepository implements TestStepRepository {
    private final SpringDataTestStepJpaRepository springData;
    private final TestStepPersistenceMapper mapper;
    public JpaTestStepRepository(SpringDataTestStepJpaRepository springData, TestStepPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public TestStep save(TestStep e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestStep> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<TestStep> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

    @Override public java.util.List<TestStep> findByProjectIdAndTestCaseId(UUID projectId, UUID parentId) {
        return springData.findByProjectIdAndTestCaseIdOrderByStepOrderAsc(projectId, parentId).stream().map(mapper::toDomain).toList();
    }

}
