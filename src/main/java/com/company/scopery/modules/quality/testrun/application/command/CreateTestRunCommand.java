package com.company.scopery.modules.quality.testrun.application.command;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import java.util.List; import java.util.UUID;
public record CreateTestRunCommand(UUID projectId, String name, String runType, String runScope,
        UUID testPlanId, UUID testSuiteId, UUID releasePackageId, List<CaseRef> caseIds) {
    public record CaseRef(MembershipCaseKind caseKind, UUID caseId) {}
    public CreateTestRunCommand(UUID projectId, String name, String runType, String runScope,
            UUID testPlanId, UUID testSuiteId, UUID releasePackageId) {
        this(projectId, name, runType, runScope, testPlanId, testSuiteId, releasePackageId, List.of());
    }
}
