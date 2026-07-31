package com.company.scopery.modules.quality.verificationcase.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.verificationcase.application.command.UpdateVerificationCaseCommand;
import com.company.scopery.modules.quality.verificationcase.application.response.VerificationCaseResponse;
import com.company.scopery.modules.quality.verificationcase.domain.enums.*;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateVerificationCaseAction {
    private final VerificationCaseRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public UpdateVerificationCaseAction(VerificationCaseRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public VerificationCaseResponse execute(UpdateVerificationCaseCommand c) {
        authorization.requireQualityUpdate(c.projectId());
        var vc = repo.findByIdAndProjectId(c.verificationCaseId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.verificationCaseNotFound(c.verificationCaseId()));
        if (c.version() != null && c.version() != vc.version()) throw QualityExceptions.staleVersion();
        var method = c.verificationMethod() != null
                ? QualityEnumParser.parseRequired(VerificationMethod.class, c.verificationMethod(), "verificationMethod") : null;
        var status = c.lifecycleStatus() != null
                ? QualityEnumParser.parseRequired(VerificationCaseStatus.class, c.lifecycleStatus(), "lifecycleStatus") : null;
        var automation = c.automationStatus() != null
                ? QualityEnumParser.parseRequired(VerificationAutomationStatus.class, c.automationStatus(), "automationStatus") : null;
        var updated = vc.update(c.title(), c.description(), method, c.procedure(), c.expectedResultJson(),
                c.environment(), status, automation, c.ownerId(), c.assigneeId());
        var saved = repo.save(updated);
        activityLogger.logSuccess(QualityEntityTypes.VERIFICATION_CASE, saved.id(),
                QualityActivityActions.VERIFICATION_CASE_UPDATED, "Verification case updated");
        return VerificationCaseResponse.from(saved);
    }
}
