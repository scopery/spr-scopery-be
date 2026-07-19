package com.company.scopery.modules.quality.testrun.infrastructure.mapper;
import com.company.scopery.modules.quality.testrun.domain.enums.*; import com.company.scopery.modules.quality.testrun.domain.model.TestRun;
import com.company.scopery.modules.quality.testrun.infrastructure.persistence.TestRunJpaEntity; import org.springframework.stereotype.Component;
@Component
public class TestRunPersistenceMapper {
    public TestRun toDomain(TestRunJpaEntity e) {
        return new TestRun(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getTestPlanId(), e.getTestSuiteId(), e.getReleasePackageId(),
                e.getDeploymentEnvironmentId(), e.getName(), TestRunType.valueOf(e.getRunType()), TestRunStatus.valueOf(e.getStatus()),
                e.getStartedAt(), e.getCompletedAt(), e.getExecutedBy(), e.getSummaryJson(), e.getArchivedAt(), e.getArchivedBy(),
                e.getTraceId(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TestRunJpaEntity toJpaEntity(TestRun d) {
        TestRunJpaEntity e = new TestRunJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setTestPlanId(d.testPlanId());
        e.setTestSuiteId(d.testSuiteId()); e.setReleasePackageId(d.releasePackageId()); e.setDeploymentEnvironmentId(d.deploymentEnvironmentId());
        e.setName(d.name()); e.setRunType(d.runType().name()); e.setStatus(d.status().name()); e.setStartedAt(d.startedAt());
        e.setCompletedAt(d.completedAt()); e.setExecutedBy(d.executedBy()); e.setSummaryJson(d.summaryJson());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setTraceId(d.traceId()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
