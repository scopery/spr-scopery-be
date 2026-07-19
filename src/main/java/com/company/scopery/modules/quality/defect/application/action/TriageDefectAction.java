package com.company.scopery.modules.quality.defect.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class TriageDefectAction {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;
    public TriageDefectAction(DefectRepository repo, QualityAuthorizationService authorization,
                  CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.currentUser = currentUser; this.activityLogger = activityLogger;
    }
    @Transactional
    public DefectResponse execute(UUID projectId, UUID defectId) {
        authorization.requireDefectUpdate(projectId);
        var actor = currentUser.resolveCurrentUser();
        var d = repo.findByIdAndProjectId(defectId, projectId).orElseThrow(() -> QualityExceptions.defectNotFound(defectId));
        var saved = repo.save(d.triage());
        activityLogger.logSuccess(QualityEntityTypes.DEFECT, saved.id(), QualityActivityActions.DEFECT_TRIAGED, "Defect triaged");
        return DefectResponse.from(saved);
    }
}
