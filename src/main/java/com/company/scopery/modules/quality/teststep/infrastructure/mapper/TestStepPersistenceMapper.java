package com.company.scopery.modules.quality.teststep.infrastructure.mapper;
import com.company.scopery.modules.quality.teststep.domain.model.TestStep;
import com.company.scopery.modules.quality.teststep.infrastructure.persistence.TestStepJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestStepPersistenceMapper {
    public TestStep toDomain(TestStepJpaEntity e) { return new TestStep(e.getId(), e.getProjectId(), e.getTestCaseId(), e.getStepOrder(), e.getActionText(), e.getExpectedResult(), e.getDataNotes(), e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt()); }
    public TestStepJpaEntity toJpaEntity(TestStep d) {
        TestStepJpaEntity e = new TestStepJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestCaseId(d.testCaseId()); e.setStepOrder(d.stepOrder());
        e.setActionText(d.actionText()); e.setExpectedResult(d.expectedResult()); e.setDataNotes(d.dataNotes());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
