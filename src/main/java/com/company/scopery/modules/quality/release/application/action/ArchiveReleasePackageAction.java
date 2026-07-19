package com.company.scopery.modules.quality.release.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.quality.release.application.command.ArchiveReleasePackageCommand;
import com.company.scopery.modules.quality.release.application.response.ReleasePackageResponse;
import com.company.scopery.modules.quality.release.domain.model.ReleasePackageRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveReleasePackageAction {
    private final ReleasePackageRepository repo;
    private final QualityAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;

    public ArchiveReleasePackageAction(ReleasePackageRepository repo, QualityAuthorizationService authorization,
                                       CurrentUserAuthorizationService currentUser) {
        this.repo = repo;
        this.authorization = authorization;
        this.currentUser = currentUser;
    }

    @Transactional
    public ReleasePackageResponse execute(ArchiveReleasePackageCommand c) {
        authorization.requireReleaseUpdate(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var rel = repo.findByIdAndProjectId(c.releasePackageId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.releaseNotFound(c.releasePackageId()));
        return ReleasePackageResponse.from(repo.save(rel.archive(actor.id())));
    }
}
