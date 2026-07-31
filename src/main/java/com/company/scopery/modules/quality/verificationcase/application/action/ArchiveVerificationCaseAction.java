package com.company.scopery.modules.quality.verificationcase.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.verificationcase.application.response.VerificationCaseResponse;
import com.company.scopery.modules.quality.verificationcase.domain.enums.VerificationCaseStatus;
import com.company.scopery.modules.quality.verificationcase.domain.model.VerificationCaseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ArchiveVerificationCaseAction {
    private final VerificationCaseRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public ArchiveVerificationCaseAction(VerificationCaseRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public VerificationCaseResponse execute(UUID projectId, UUID verificationCaseId) {
        authorization.requireQualityUpdate(projectId);
        var vc = repo.findByIdAndProjectId(verificationCaseId, projectId)
                .orElseThrow(() -> QualityExceptions.verificationCaseNotFound(verificationCaseId));
        if (vc.lifecycleStatus() == VerificationCaseStatus.ARCHIVED)
            throw QualityExceptions.verificationCaseAlreadyArchived(verificationCaseId);
        var archived = vc.archive(null);
        var saved = repo.save(archived);
        activityLogger.logSuccess(QualityEntityTypes.VERIFICATION_CASE, saved.id(),
                QualityActivityActions.VERIFICATION_CASE_ARCHIVED, "Verification case archived");
        return VerificationCaseResponse.from(saved);
    }
}
