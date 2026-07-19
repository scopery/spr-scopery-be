package com.company.scopery.modules.quality.release.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.release.application.command.CreateReleasePackageCommand;
import com.company.scopery.modules.quality.release.application.response.ReleasePackageResponse;
import com.company.scopery.modules.quality.release.domain.enums.ReleaseType; import com.company.scopery.modules.quality.release.domain.model.*;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger; import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.*; import com.company.scopery.modules.quality.shared.error.QualityExceptions; import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateReleasePackageAction {
    private final ProjectRepository projects; private final ReleasePackageRepository repo;
    private final QualityAuthorizationService authorization; private final QualityActivityLogger activityLogger;
    public CreateReleasePackageAction(ProjectRepository projects, ReleasePackageRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public ReleasePackageResponse execute(CreateReleasePackageCommand c) {
        authorization.requireReleaseCreate(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        if (repo.existsByProjectIdAndCode(c.projectId(), c.code())) throw QualityExceptions.releaseCodeExists(c.code());
        var saved = repo.save(ReleasePackage.create(project.id(), project.workspaceId(), c.code(), c.versionLabel(), c.name().trim(), c.description(),
                QualityEnumParser.parseRequired(ReleaseType.class, c.releaseType(), "releaseType"), c.plannedReleaseDate()));
        activityLogger.logSuccess(QualityEntityTypes.RELEASE_PACKAGE, saved.id(), QualityActivityActions.RELEASE_PACKAGE_CREATED, "Release package created");
        return ReleasePackageResponse.from(saved);
    }
}
