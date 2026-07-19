package com.company.scopery.modules.quality.testplan.infrastructure.persistence;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlan;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlanRepository;
import com.company.scopery.modules.quality.testplan.infrastructure.mapper.TestPlanPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaTestPlanRepository implements TestPlanRepository {
    private final SpringDataTestPlanJpaRepository springData;
    private final TestPlanPersistenceMapper mapper;
    public JpaTestPlanRepository(SpringDataTestPlanJpaRepository springData, TestPlanPersistenceMapper mapper) {
        this.springData = springData; this.mapper = mapper;
    }
    @Override public TestPlan save(TestPlan e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestPlan> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<TestPlan> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }

}
