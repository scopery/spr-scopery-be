package com.company.scopery.modules.quality.coverage.infrastructure.mapper;
import com.company.scopery.modules.quality.coverage.domain.enums.CoverageType;
import com.company.scopery.modules.quality.coverage.domain.model.TestCaseCoverage;
import com.company.scopery.modules.quality.coverage.infrastructure.persistence.TestCaseCoverageJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestCaseCoveragePersistenceMapper {
    public TestCaseCoverage toDomain(TestCaseCoverageJpaEntity e) {
        return new TestCaseCoverage(e.getId(), e.getProjectId(), e.getTestCaseId(), e.getTargetType(), e.getTargetId(),
                CoverageType.valueOf(e.getCoverageType()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt());
    }
    public TestCaseCoverageJpaEntity toJpaEntity(TestCaseCoverage d) {
        TestCaseCoverageJpaEntity e = new TestCaseCoverageJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestCaseId(d.testCaseId());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setCoverageType(d.coverageType().name());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
