package com.company.scopery.modules.quality.testcase.infrastructure.mapper;
import com.company.scopery.modules.quality.testcase.domain.enums.*; import com.company.scopery.modules.quality.testcase.domain.model.TestCase;
import com.company.scopery.modules.quality.testcase.infrastructure.persistence.TestCaseJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestCasePersistenceMapper {
    public TestCase toDomain(TestCaseJpaEntity e) {
        return new TestCase(e.getId(), e.getProjectId(), e.getTestSuiteId(), e.getCode(), e.getTitle(), e.getDescription(),
                TestCaseType.valueOf(e.getType()), TestCasePriority.valueOf(e.getPriority()), TestCaseStatus.valueOf(e.getStatus()),
                e.getPreconditions(), e.getExpectedResult(), e.getVersionNumber(), e.getApprovedAt(), e.getApprovedBy(),
                e.getArchivedAt(), e.getArchivedBy(), e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
    public TestCaseJpaEntity toJpaEntity(TestCase d) {
        TestCaseJpaEntity e = new TestCaseJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestSuiteId(d.testSuiteId()); e.setCode(d.code());
        e.setTitle(d.title()); e.setDescription(d.description()); e.setType(d.type().name()); e.setPriority(d.priority().name());
        e.setStatus(d.status().name()); e.setPreconditions(d.preconditions()); e.setExpectedResult(d.expectedResult());
        e.setVersionNumber(d.versionNumber()); e.setApprovedAt(d.approvedAt()); e.setApprovedBy(d.approvedBy());
        e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy()); e.setVersion(d.version());
        if (d.createdAt()!=null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
