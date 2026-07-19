package com.company.scopery.modules.quality.release.application.action;

import com.company.scopery.modules.quality.release.application.command.MarkReadyReleaseCommand;
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
public class MarkReadyReleaseAction {
    private final ReleasePackageRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;

    public MarkReadyReleaseAction(ReleasePackageRepository repo, QualityAuthorizationService authorization,
                                  QualityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ReleasePackageResponse execute(MarkReadyReleaseCommand c) {
        authorization.requireReleaseApprove(c.projectId());
        var rel = repo.findByIdAndProjectId(c.releasePackageId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.releaseNotFound(c.releasePackageId()));
        try {
            var saved = repo.save(rel.markReady());
            activityLogger.logSuccess(QualityEntityTypes.RELEASE_PACKAGE, saved.id(),
                    QualityActivityActions.RELEASE_READY, "Release marked ready");
            return ReleasePackageResponse.from(saved);
        } catch (IllegalStateException ex) {
            throw QualityExceptions.releaseNotReady("Blocking readiness checks failed");
        }
    }
}
