package com.company.scopery.modules.quality.testrun.domain.model;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import java.util.List; import java.util.UUID;
public interface TestRunMembershipRepository {
    TestRunMembershipItem save(TestRunMembershipItem item);
    List<TestRunMembershipItem> findByTestRunId(UUID testRunId);
    boolean exists(UUID testRunId, MembershipCaseKind caseKind, UUID caseId);
    void delete(UUID testRunId, MembershipCaseKind caseKind, UUID caseId);
    void deleteAllByTestRunId(UUID testRunId);
    int countByTestRunId(UUID testRunId);
}
