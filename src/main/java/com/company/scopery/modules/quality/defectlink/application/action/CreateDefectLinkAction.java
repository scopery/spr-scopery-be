package com.company.scopery.modules.quality.defectlink.application.action;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quality.defectlink.application.command.CreateDefectLinkCommand;
import com.company.scopery.modules.quality.defectlink.application.response.DefectLinkResponse;
import com.company.scopery.modules.quality.defectlink.domain.enums.DefectLinkType;
import com.company.scopery.modules.quality.defectlink.domain.model.DefectLink;
import com.company.scopery.modules.quality.defectlink.domain.model.DefectLinkRepository;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.error.QualityExceptions;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDefectLinkAction {
    private final ProjectRepository projects;
    private final DefectLinkRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public CreateDefectLinkAction(ProjectRepository projects, DefectLinkRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public DefectLinkResponse execute(CreateDefectLinkCommand c) {
        authorization.requireDefectUpdate(c.projectId());
        var project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw QualityExceptions.projectArchived(c.projectId());
        var lt = QualityEnumParser.parseRequired(DefectLinkType.class, c.linkType(), "linkType");
        var saved = repo.save(DefectLink.create(project.id(), c.defectId(), c.targetType().trim(), c.targetId(), lt));
        activityLogger.logSuccess(QualityEntityTypes.DEFECT_LINK, saved.id(), QualityActivityActions.DEFECT_CREATED, "Defect link created");
        return DefectLinkResponse.from(saved);
    }
}
