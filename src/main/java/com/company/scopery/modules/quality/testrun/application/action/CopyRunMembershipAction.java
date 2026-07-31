package com.company.scopery.modules.quality.testrun.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.testcase.domain.model.TestCaseRepository;
import com.company.scopery.modules.quality.testrun.application.command.CopyRunMembershipCommand;
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
@Component
public class CopyRunMembershipAction {
    private final TestRunRepository runs;
    private final TestRunMembershipRepository membership;
    private final TestCaseRepository testCases;
    private final VerificationCaseRepository verificationCases;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    private final ManageRunMembershipAction manage;
    public CopyRunMembershipAction(TestRunRepository runs, TestRunMembershipRepository membership,
            TestCaseRepository testCases, VerificationCaseRepository verificationCases,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger,
            ManageRunMembershipAction manage) {
        this.runs=runs; this.membership=membership; this.testCases=testCases;
        this.verificationCases=verificationCases; this.authorization=authorization;
        this.activityLogger=activityLogger; this.manage=manage;
    }
    @Transactional
    public RunMembershipResponse execute(CopyRunMembershipCommand cmd) {
        authorization.requireTestCreate(cmd.projectId());
        var target = runs.findByIdAndProjectId(cmd.testRunId(), cmd.projectId())
                .orElseThrow(() -> QualityExceptions.testRunNotFound(cmd.testRunId()));
        if (target.status() == TestRunStatus.COMPLETED || target.status() == TestRunStatus.CANCELLED)
            throw QualityExceptions.runMembershipRunClosed(target.id());
        var source = runs.findByIdAndProjectId(cmd.sourceRunId(), cmd.projectId())
                .orElseThrow(() -> QualityExceptions.testRunNotFound(cmd.sourceRunId()));

        if (cmd.replaceExisting()) membership.deleteAllByTestRunId(target.id());

        for (var item : membership.findByTestRunId(source.id())) {
            if (!isScopeCompatible(target.runScope(), item.caseKind())) continue;
            if (membership.exists(target.id(), item.caseKind(), item.caseId())) continue;
            int order = membership.countByTestRunId(target.id());
            membership.save(TestRunMembershipItem.create(cmd.projectId(), target.id(), item.caseKind(), item.caseId(), order));
        }

        activityLogger.logSuccess(QualityEntityTypes.RUN_MEMBERSHIP, target.id(), QualityActivityActions.RUN_MEMBERSHIP_COPIED,
                "Run membership copied from " + source.id());
        return manage.buildResponse(target.id(), cmd.projectId());
    }

    private boolean isScopeCompatible(RunScope scope, MembershipCaseKind kind) {
        if (scope == RunScope.FUNCTIONAL) return kind == MembershipCaseKind.FUNCTIONAL;
        if (scope == RunScope.NON_FUNCTIONAL) return kind == MembershipCaseKind.NFR;
        return true;
    }
}
