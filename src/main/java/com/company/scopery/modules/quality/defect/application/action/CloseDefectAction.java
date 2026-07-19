package com.company.scopery.modules.quality.defect.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.defect.application.command.CloseDefectCommand;
import com.company.scopery.modules.quality.defect.application.response.DefectResponse;
import com.company.scopery.modules.quality.defect.domain.enums.DefectResolutionType;
import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CloseDefectAction {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;

    public CloseDefectAction(DefectRepository repo, QualityAuthorizationService authorization,
                             CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DefectResponse execute(CloseDefectCommand c) {
        authorization.requireDefectResolve(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var d = repo.findByIdAndProjectId(c.defectId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.defectNotFound(c.defectId()));
        try {
            var saved = repo.save(d.close(
                    actor.id(),
                    QualityEnumParser.parseRequired(DefectResolutionType.class, c.resolutionType(), "resolutionType"),
                    c.resolutionNote()));
            activityLogger.logSuccess(QualityEntityTypes.DEFECT, saved.id(), QualityActivityActions.DEFECT_CLOSED, "Defect closed");
            return DefectResponse.from(saved);
        } catch (IllegalArgumentException ex) {
            throw QualityExceptions.defectResolutionRequired();
        }
    }
}
