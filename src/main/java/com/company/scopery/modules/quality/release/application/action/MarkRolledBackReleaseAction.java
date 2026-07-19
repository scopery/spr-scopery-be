package com.company.scopery.modules.quality.release.application.action;

import com.company.scopery.modules.quality.release.application.command.MarkRolledBackCommand;
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
public class MarkRolledBackReleaseAction {
    private final ReleasePackageRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;

    public MarkRolledBackReleaseAction(ReleasePackageRepository repo, QualityAuthorizationService authorization,
                                       QualityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ReleasePackageResponse execute(MarkRolledBackCommand c) {
        authorization.requireReleaseUpdate(c.projectId());
        var rel = repo.findByIdAndProjectId(c.releasePackageId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.releaseNotFound(c.releasePackageId()));
        var saved = repo.save(rel.markRolledBack());
        activityLogger.logSuccess(QualityEntityTypes.RELEASE_PACKAGE, saved.id(),
                QualityActivityActions.RELEASE_ROLLED_BACK, "Release rolled back");
        return ReleasePackageResponse.from(saved);
    }
}
