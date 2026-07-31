package com.company.scopery.modules.quality.testrun.domain.model;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import java.time.Instant; import java.util.UUID;
public record TestRunMembershipItem(UUID id, UUID projectId, UUID testRunId, MembershipCaseKind caseKind, UUID caseId, int displayOrder, Instant createdAt) {
    public static TestRunMembershipItem create(UUID projectId, UUID testRunId, MembershipCaseKind caseKind, UUID caseId, int displayOrder) {
        return new TestRunMembershipItem(UUID.randomUUID(), projectId, testRunId, caseKind, caseId, displayOrder, Instant.now());
    }
}
