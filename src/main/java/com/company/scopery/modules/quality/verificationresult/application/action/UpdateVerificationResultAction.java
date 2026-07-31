package com.company.scopery.modules.quality.verificationresult.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.verificationresult.application.command.UpdateVerificationResultCommand;
import com.company.scopery.modules.quality.verificationresult.application.response.VerificationCaseResultResponse;
import com.company.scopery.modules.quality.verificationresult.domain.enums.VerificationResultStatus;
import com.company.scopery.modules.quality.verificationresult.domain.model.VerificationCaseResultRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateVerificationResultAction {
    private final VerificationCaseResultRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public UpdateVerificationResultAction(VerificationCaseResultRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public VerificationCaseResultResponse execute(UpdateVerificationResultCommand c) {
        authorization.requireTestUpdate(c.projectId());
        var existing = repo.findByIdAndProjectId(c.resultId(), c.projectId()).orElseThrow(() -> QualityExceptions.verificationResultNotFound(c.resultId()));
        if (existing.version() != c.version()) throw QualityExceptions.staleVersion();
        var status = QualityEnumParser.parseOptional(VerificationResultStatus.class, c.resultStatus(), "resultStatus");
        var updated = existing.update(status, c.actualValue(), c.actualValueUnit(), c.actualResultJson(), c.evidenceReference(), c.executedById(), c.defectId(), c.comment());
        var saved = repo.save(updated);
        activityLogger.logSuccess(QualityEntityTypes.VERIFICATION_CASE_RESULT, saved.id(), QualityActivityActions.VERIFICATION_RESULT_UPDATED, "Verification result updated");
        return VerificationCaseResultResponse.from(saved);
    }
}
