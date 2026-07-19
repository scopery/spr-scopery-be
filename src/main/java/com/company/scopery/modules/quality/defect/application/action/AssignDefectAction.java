package com.company.scopery.modules.quality.defect.application.action;

import com.company.scopery.modules.quality.defect.application.command.AssignDefectCommand;
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
public class AssignDefectAction {
    private final DefectRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;

    public AssignDefectAction(DefectRepository repo, QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DefectResponse execute(AssignDefectCommand c) {
        authorization.requireDefectUpdate(c.projectId());
        var d = repo.findByIdAndProjectId(c.defectId(), c.projectId())
                .orElseThrow(() -> QualityExceptions.defectNotFound(c.defectId()));
        var saved = repo.save(d.assign(c.assignedToUserId()));
        activityLogger.logSuccess(QualityEntityTypes.DEFECT, saved.id(), QualityActivityActions.DEFECT_ASSIGNED, "Defect assigned");
        return DefectResponse.from(saved);
    }
}
