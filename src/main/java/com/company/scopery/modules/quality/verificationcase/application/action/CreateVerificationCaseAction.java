package com.company.scopery.modules.quality.verificationcase.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.verificationcase.application.command.CreateVerificationCaseCommand;
import com.company.scopery.modules.quality.verificationcase.application.response.VerificationCaseResponse;
import com.company.scopery.modules.quality.verificationcase.domain.enums.*;
import com.company.scopery.modules.quality.verificationcase.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateVerificationCaseAction {
    private final VerificationCaseRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateVerificationCaseAction(VerificationCaseRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public VerificationCaseResponse execute(CreateVerificationCaseCommand c) {
        authorization.requireQualityCreate(c.projectId());
        var method = QualityEnumParser.parseRequired(VerificationMethod.class, c.verificationMethod(), "verificationMethod");
        var automation = QualityEnumParser.parseOptional(VerificationAutomationStatus.class, c.automationStatus(), "automationStatus");
        var saved = repo.save(VerificationCase.create(c.projectId(), c.requirementId(), c.code(),
                c.title().trim(), c.description(), method, c.procedure(), c.expectedResultJson(),
                c.environment(), automation, c.ownerId(), c.assigneeId()));
        activityLogger.logSuccess(QualityEntityTypes.VERIFICATION_CASE, saved.id(),
                QualityActivityActions.VERIFICATION_CASE_CREATED, "Verification case created");
        return VerificationCaseResponse.from(saved);
    }
}
