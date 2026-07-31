package com.company.scopery.modules.quality.teststep.infrastructure.mapper;
import com.company.scopery.modules.quality.teststep.domain.model.TestCaseStep;
import com.company.scopery.modules.quality.teststep.infrastructure.persistence.TestCaseStepJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestCaseStepPersistenceMapper {
    public TestCaseStep toDomain(TestCaseStepJpaEntity e) {
        return new TestCaseStep(e.getId(), e.getTestCaseId(), e.getProjectId(), e.getSortOrder(),
                e.getAction(), e.getExpectedResult(), e.getScreenId(), e.getComponentId(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(),
                e.getCreatedAt(), e.getUpdatedAt());
    }
    public TestCaseStepJpaEntity toJpaEntity(TestCaseStep d) {
        TestCaseStepJpaEntity e = new TestCaseStepJpaEntity();
        e.setId(d.id()); e.setTestCaseId(d.testCaseId()); e.setProjectId(d.projectId());
        e.setSortOrder(d.sortOrder()); e.setAction(d.action()); e.setExpectedResult(d.expectedResult());
        e.setScreenId(d.screenId()); e.setComponentId(d.componentId());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version() >= 0 ? d.version() : null);
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
