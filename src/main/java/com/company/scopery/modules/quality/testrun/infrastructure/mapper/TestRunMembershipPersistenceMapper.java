package com.company.scopery.modules.quality.testrun.infrastructure.mapper;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunMembershipItem;
import com.company.scopery.modules.quality.testrun.infrastructure.persistence.TestRunMembershipJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class TestRunMembershipPersistenceMapper {
    public TestRunMembershipItem toDomain(TestRunMembershipJpaEntity e) {
        return new TestRunMembershipItem(e.getId(), e.getProjectId(), e.getTestRunId(),
                MembershipCaseKind.valueOf(e.getCaseKind()), e.getCaseId(), e.getDisplayOrder(), e.getCreatedAt());
    }
    public TestRunMembershipJpaEntity toJpaEntity(TestRunMembershipItem d) {
        TestRunMembershipJpaEntity e = new TestRunMembershipJpaEntity();
        e.setId(d.id()); e.setProjectId(d.projectId()); e.setTestRunId(d.testRunId());
        e.setCaseKind(d.caseKind().name()); e.setCaseId(d.caseId());
        e.setDisplayOrder(d.displayOrder()); e.setCreatedAt(d.createdAt());
        return e;
    }
}
