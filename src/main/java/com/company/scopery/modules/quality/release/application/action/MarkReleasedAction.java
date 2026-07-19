package com.company.scopery.modules.quality.release.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.release.application.command.MarkReleasedCommand;
import com.company.scopery.modules.quality.release.application.response.ReleasePackageResponse;
import com.company.scopery.modules.quality.release.domain.model.ReleasePackageRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MarkReleasedAction {
    private final ReleasePackageRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QualityActivityLogger activityLogger;

    public MarkReleasedAction(ReleasePackageRepository repo, QualityAuthorizationService authorization,
                              CurrentUserAuthorizationService currentUser, QualityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ReleasePackageResponse execute(MarkReleasedCommand c) {
        authorization.requireReleaseApprove(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var rel = repo.findByIdAndProjectId(c.releasePackageId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.releaseNotFound(c.releasePackageId()));
        try {
            var saved = repo.save(rel.markReleased(actor.id()));
            activityLogger.logSuccess(QualityEntityTypes.RELEASE_PACKAGE, saved.id(),
                    QualityActivityActions.RELEASE_RELEASED, "Release marked released");
            return ReleasePackageResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw QualityExceptions.releaseInvalidStatus(ex.getMessage());
        }
    }
}
