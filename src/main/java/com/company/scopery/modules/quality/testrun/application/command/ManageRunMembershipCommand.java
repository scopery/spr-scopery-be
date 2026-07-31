package com.company.scopery.modules.quality.testrun.application.command;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import java.util.List; import java.util.UUID;
public record ManageRunMembershipCommand(UUID projectId, UUID testRunId, List<CaseRef> add, List<CaseRef> remove) {
    public record CaseRef(MembershipCaseKind caseKind, UUID caseId) {}
}
