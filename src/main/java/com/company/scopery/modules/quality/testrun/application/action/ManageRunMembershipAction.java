package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.testrun.application.command.ManageRunMembershipCommand;
import com.company.scopery.modules.quality.testrun.application.response.RunMembershipItemResponse;
import com.company.scopery.modules.quality.testrun.application.response.RunMembershipResponse;
import com.company.scopery.modules.quality.testrun.domain.enums.MembershipCaseKind;
import com.company.scopery.modules.quality.testrun.domain.enums.RunScope;
import com.company.scopery.modules.quality.testrun.domain.enums.TestRunStatus;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunMembershipItem;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunMembershipRepository;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;
@Component
public class ManageRunMembershipAction {
    private final TestRunRepository runs;
    private final TestRunMembershipRepository membership;
    private final TestCaseRepository testCases;
    private final VerificationCaseRepository verificationCases;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public ManageRunMembershipAction(TestRunRepository runs, TestRunMembershipRepository membership,
            TestCaseRepository testCases, VerificationCaseRepository verificationCases,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.runs=runs; this.membership=membership; this.testCases=testCases;
        this.verificationCases=verificationCases; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RunMembershipResponse execute(ManageRunMembershipCommand cmd) {
        authorization.requireTestCreate(cmd.projectId());
        var run = runs.findByIdAndProjectId(cmd.testRunId(), cmd.projectId())
                .orElseThrow(() -> QualityExceptions.testRunNotFound(cmd.testRunId()));
        if (run.status() == TestRunStatus.COMPLETED || run.status() == TestRunStatus.CANCELLED)
            throw QualityExceptions.runMembershipRunClosed(run.id());

        List<ManageRunMembershipCommand.CaseRef> adds = cmd.add() != null ? cmd.add() : List.of();
        for (var ref : adds) {
            validateScopeCompat(run.runScope(), ref.caseKind());
            validateCaseBelongsToProject(cmd.projectId(), ref.caseKind(), ref.caseId());
            if (!membership.exists(run.id(), ref.caseKind(), ref.caseId())) {
                int order = membership.countByTestRunId(run.id());
                membership.save(TestRunMembershipItem.create(cmd.projectId(), run.id(), ref.caseKind(), ref.caseId(), order));
            }
        }

        List<ManageRunMembershipCommand.CaseRef> removes = cmd.remove() != null ? cmd.remove() : List.of();
        for (var ref : removes) {
            membership.delete(run.id(), ref.caseKind(), ref.caseId());
        }

        activityLogger.logSuccess(QualityEntityTypes.RUN_MEMBERSHIP, run.id(), QualityActivityActions.RUN_MEMBERSHIP_UPDATED, "Run membership updated");
        return buildResponse(run.id(), cmd.projectId());
    }

    private void validateScopeCompat(RunScope scope, MembershipCaseKind kind) {
        if (scope == RunScope.FUNCTIONAL && kind != MembershipCaseKind.FUNCTIONAL)
            throw QualityExceptions.runMembershipScopeMismatch(kind.name(), scope.name());
        if (scope == RunScope.NON_FUNCTIONAL && kind != MembershipCaseKind.NFR)
            throw QualityExceptions.runMembershipScopeMismatch(kind.name(), scope.name());
    }

    private void validateCaseBelongsToProject(UUID projectId, MembershipCaseKind kind, UUID caseId) {
        if (kind == MembershipCaseKind.FUNCTIONAL) {
            testCases.findByIdAndProjectId(caseId, projectId)
                    .orElseThrow(() -> QualityExceptions.runMembershipCaseNotFound(caseId));
        } else {
            verificationCases.findByIdAndProjectId(caseId, projectId)
                    .orElseThrow(() -> QualityExceptions.runMembershipCaseNotFound(caseId));
        }
    }

    RunMembershipResponse buildResponse(UUID runId, UUID projectId) {
        var items = membership.findByTestRunId(runId).stream()
                .map(item -> enrichItem(item, projectId)).toList();
        return new RunMembershipResponse(runId, items);
    }

    RunMembershipItemResponse enrichItem(TestRunMembershipItem item, UUID projectId) {
        String code = null, title = null;
        if (item.caseKind() == MembershipCaseKind.FUNCTIONAL) {
            var tc = testCases.findByIdAndProjectId(item.caseId(), projectId).orElse(null);
            if (tc != null) { code = tc.code(); title = tc.title(); }
        } else {
            var vc = verificationCases.findByIdAndProjectId(item.caseId(), projectId).orElse(null);
            if (vc != null) { code = vc.code(); title = vc.title(); }
        }
        return new RunMembershipItemResponse(item.caseKind().name(), item.caseId(), code, title, null, item.displayOrder());
    }
}
