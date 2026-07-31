package com.company.scopery.modules.quality.verificationresult.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.testrun.domain.model.TestRunRepository;
import com.company.scopery.modules.quality.verificationresult.application.command.RecordVerificationResultCommand;
import com.company.scopery.modules.quality.verificationresult.application.response.VerificationCaseResultResponse;
import com.company.scopery.modules.quality.verificationresult.domain.enums.VerificationResultStatus;
import com.company.scopery.modules.quality.verificationresult.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class RecordVerificationResultAction {
    private final VerificationCaseResultRepository repo;
    private final TestRunRepository testRunRepo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public RecordVerificationResultAction(VerificationCaseResultRepository repo, TestRunRepository testRunRepo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.testRunRepo=testRunRepo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public VerificationCaseResultResponse execute(RecordVerificationResultCommand c) {
        authorization.requireTestUpdate(c.projectId());
        testRunRepo.findByIdAndProjectId(c.testRunId(), c.projectId()).orElseThrow(() -> QualityExceptions.testRunNotFound(c.testRunId()));
        var status = QualityEnumParser.parseOptional(VerificationResultStatus.class, c.resultStatus(), "resultStatus");
        var existing = repo.findByTestRunIdAndVerificationCaseId(c.testRunId(), c.verificationCaseId());
        VerificationCaseResult result;
        if (existing.isPresent()) {
            result = existing.get().update(status, c.actualValue(), c.actualValueUnit(), c.actualResultJson(), c.evidenceReference(), c.executedById(), c.defectId(), c.comment());
        } else {
            result = VerificationCaseResult.create(c.projectId(), c.testRunId(), c.verificationCaseId(), status, c.actualValue(), c.actualValueUnit(), c.actualResultJson(), c.evidenceReference(), c.executedById(), c.defectId(), c.comment());
        }
        var saved = repo.save(result);
        activityLogger.logSuccess(QualityEntityTypes.VERIFICATION_CASE_RESULT, saved.id(), QualityActivityActions.VERIFICATION_RESULT_RECORDED, "Verification result recorded");
        return VerificationCaseResultResponse.from(saved);
    }
}
