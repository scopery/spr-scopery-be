package com.company.scopery.modules.quality.defect.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.defect.application.command.ReopenDefectCommand;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReopenDefectAction {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;

    public ReopenDefectAction(DefectRepository repo, QualityAuthorizationService authorization,
                              CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DefectResponse execute(ReopenDefectCommand c) {
        authorization.requireDefectUpdate(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var d = repo.findByIdAndProjectId(c.defectId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.defectNotFound(c.defectId()));
        try {
            var saved = repo.save(d.reopen(actor.id(), c.reason()));
            activityLogger.logSuccess(QualityEntityTypes.DEFECT, saved.id(), QualityActivityActions.DEFECT_REOPENED, "Defect reopened");
            return DefectResponse.from(saved);
        } catch (IllegalArgumentException ex) {
            throw QualityExceptions.defectReopenReasonRequired();
        }
    }
}
