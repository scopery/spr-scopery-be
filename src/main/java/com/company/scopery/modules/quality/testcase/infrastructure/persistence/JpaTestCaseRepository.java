package com.company.scopery.modules.quality.testcase.infrastructure.persistence;
import com.company.scopery.modules.quality.testcase.domain.model.*; import com.company.scopery.modules.quality.testcase.infrastructure.mapper.TestCasePersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaTestCaseRepository implements TestCaseRepository {
    private final SpringDataTestCaseJpaRepository springData; private final TestCasePersistenceMapper mapper;
    public JpaTestCaseRepository(SpringDataTestCaseJpaRepository springData, TestCasePersistenceMapper mapper) { this.springData=springData; this.mapper=mapper; }
    @Override public TestCase save(TestCase e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<TestCase> findByIdAndProjectId(UUID id, UUID projectId) { return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain); }
    @Override public List<TestCase> findByProjectId(UUID projectId) { return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByProjectIdAndCode(UUID projectId, String code) { return code != null && springData.existsByProjectIdAndCode(projectId, code); }
}
