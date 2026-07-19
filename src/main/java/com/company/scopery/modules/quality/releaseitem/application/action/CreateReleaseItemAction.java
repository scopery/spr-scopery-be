package com.company.scopery.modules.quality.releaseitem.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.releaseitem.application.command.CreateReleaseItemCommand;
import com.company.scopery.modules.quality.releaseitem.application.response.ReleaseItemResponse;
import com.company.scopery.modules.quality.releaseitem.domain.enums.ReleaseItemStatus;
import com.company.scopery.modules.quality.releaseitem.domain.model.ReleaseItem;
import com.company.scopery.modules.quality.releaseitem.domain.model.ReleaseItemRepository;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateReleaseItemAction {
    private final ProjectRepository projects;
    private final ReleaseItemRepository repo;
    private final QualityAuthorizationService authorization;
    public CreateReleaseItemAction(ProjectRepository projects, ReleaseItemRepository repo, QualityAuthorizationService authorization) {
        this.projects=projects; this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public ReleaseItemResponse execute(CreateReleaseItemCommand c) {
        authorization.requireReleaseUpdate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var st = QualityEnumParser.parseRequired(ReleaseItemStatus.class, c.status(), "status");
        boolean required = c.required() == null || c.required();
        return ReleaseItemResponse.from(repo.save(ReleaseItem.create(project.id(), c.releasePackageId(), c.targetType().trim(), c.targetId(), required, st, c.notes())));
    }
}
