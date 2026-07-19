package com.company.scopery.modules.quality.testplan.infrastructure.mapper;
import com.company.scopery.modules.quality.testplan.domain.enums.TestPlanStatus;
import com.company.scopery.modules.quality.testplan.domain.enums.TestLevel;
import com.company.scopery.modules.quality.testplan.domain.model.TestPlan;
import com.company.scopery.modules.quality.testplan.infrastructure.persistence.TestPlanJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestPlanPersistenceMapper {
    public TestPlan toDomain(TestPlanJpaEntity e) {
        return new TestPlan(e.getId(), e.getProjectId(), e.getWorkspaceId(), e.getQualityPlanId(), e.getReleasePackageId(),
                e.getCode(), e.getName(), e.getDescription(), TestLevel.valueOf(e.getTestLevel()), TestPlanStatus.valueOf(e.getStatus()),
                e.getApprovedAt(), e.getApprovedBy(), e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TestPlanJpaEntity toJpaEntity(TestPlan d) {
        TestPlanJpaEntity e = new TestPlanJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setWorkspaceId(d.workspaceId()); e.setQualityPlanId(d.qualityPlanId());
        e.setReleasePackageId(d.releasePackageId()); e.setCode(d.code()); e.setName(d.name()); e.setDescription(d.description());
        e.setTestLevel(d.testLevel().name()); e.setStatus(d.status().name()); e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
