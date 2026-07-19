package com.company.scopery.modules.quality.testsuite.infrastructure.mapper;
import com.company.scopery.modules.quality.testsuite.domain.enums.TestSuiteStatus;
import com.company.scopery.modules.quality.testsuite.domain.model.TestSuite;
import com.company.scopery.modules.quality.testsuite.infrastructure.persistence.TestSuiteJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestSuitePersistenceMapper {
    public TestSuite toDomain(TestSuiteJpaEntity e) { return new TestSuite(e.getId(), e.getProjectId(), e.getTestPlanId(), e.getDeliverableId(), e.getScopeItemId(), e.getName(), e.getDescription(), TestSuiteStatus.valueOf(e.getStatus()), e.getSortOrder(), e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt()); }
    public TestSuiteJpaEntity toJpaEntity(TestSuite d) {
        TestSuiteJpaEntity e = new TestSuiteJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestPlanId(d.testPlanId()); e.setDeliverableId(d.deliverableId());
        e.setScopeItemId(d.scopeItemId()); e.setName(d.name()); e.setDescription(d.description()); e.setStatus(d.status().name());
        e.setSortOrder(d.sortOrder()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
