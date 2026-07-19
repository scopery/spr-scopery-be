package com.company.scopery.modules.quality.testrun.infrastructure.mapper;
import com.company.scopery.modules.quality.testrun.domain.enums.TestResultStatus; import com.company.scopery.modules.quality.testrun.domain.model.TestCaseResult;
import com.company.scopery.modules.quality.testrun.infrastructure.persistence.TestCaseResultJpaEntity; import org.springframework.stereotype.Component;
@Component
public class TestCaseResultPersistenceMapper {
    public TestCaseResult toDomain(TestCaseResultJpaEntity e) {
        return new TestCaseResult(e.getId(), e.getProjectId(), e.getTestRunId(), e.getTestCaseId(), TestResultStatus.valueOf(e.getResultStatus()),
                e.getActualResult(), e.getEvidenceReference(), e.getExecutedAt(), e.getExecutedBy(), e.getDefectId(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TestCaseResultJpaEntity toJpaEntity(TestCaseResult d) {
        TestCaseResultJpaEntity e = new TestCaseResultJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestRunId(d.testRunId()); e.setTestCaseId(d.testCaseId());
        e.setResultStatus(d.resultStatus().name()); e.setActualResult(d.actualResult()); e.setEvidenceReference(d.evidenceReference());
        e.setExecutedAt(d.executedAt()); e.setExecutedBy(d.executedBy()); e.setDefectId(d.defectId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
