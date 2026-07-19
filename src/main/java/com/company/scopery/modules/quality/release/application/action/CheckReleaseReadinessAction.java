package com.company.scopery.modules.quality.release.application.action;

import com.company.scopery.modules.quality.defect.domain.model.DefectRepository;
import com.company.scopery.modules.quality.release.application.command.CheckReleaseReadinessCommand;
import com.company.scopery.modules.quality.release.application.response.ReleasePackageResponse;
import com.company.scopery.modules.quality.release.domain.enums.ReadinessCheckStatus;
import com.company.scopery.modules.quality.release.domain.enums.ReadinessStatus;
import com.company.scopery.modules.quality.release.domain.model.ReleasePackageRepository;
import com.company.scopery.modules.quality.release.domain.model.ReleaseReadinessCheck;
import com.company.scopery.modules.quality.release.domain.model.ReleaseReadinessCheckRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CheckReleaseReadinessAction {
    private final ReleasePackageRepository releases;
    private final ReleaseReadinessCheckRepository checks;
    private final DefectRepository defects;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;

    public CheckReleaseReadinessAction(ReleasePackageRepository releases, ReleaseReadinessCheckRepository checks,
                                       DefectRepository defects, QualityAuthorizationService authorization,
                                       QualityActivityLogger activityLogger) {
        this.releases = releases;
        this.checks = checks;
        this.defects = defects;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ReleasePackageResponse execute(CheckReleaseReadinessCommand c) {
        authorization.requireReleaseUpdate(c.projectId());
        var rel = releases.findByIdAndProjectId(c.releasePackageId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.releaseNotFound(c.releasePackageId()));
        checks.deleteByReleasePackageId(c.releasePackageId());
        var blockers = defects.findOpenBlockers(c.projectId());
        boolean failed = !blockers.isEmpty();
        checks.save(ReleaseReadinessCheck.create(
                c.projectId(), c.releasePackageId(), "NO_OPEN_BLOCKER_DEFECTS", "No open blocker/critical defects",
                failed ? ReadinessCheckStatus.FAILED : ReadinessCheckStatus.PASSED,
                failed ? blockers.size() + " open blocker/critical defects" : "No open blockers", true));
        checks.save(ReleaseReadinessCheck.create(
                c.projectId(), c.releasePackageId(), "RELEASE_PACKAGE_DEFINED", "Release package defined",
                ReadinessCheckStatus.PASSED, "Package exists", false));
        var rs = failed ? ReadinessStatus.NOT_READY : ReadinessStatus.READY;
        var summary = failed ? "{\"failed\":true,\"blockers\":" + blockers.size() + "}" : "{\"failed\":false}";
        var saved = releases.save(rel.withReadiness(rs, summary));
        activityLogger.logSuccess(QualityEntityTypes.RELEASE_PACKAGE, saved.id(),
                QualityActivityActions.RELEASE_READINESS_CHECKED, "Release readiness checked");
        return ReleasePackageResponse.from(saved);
    }
}
