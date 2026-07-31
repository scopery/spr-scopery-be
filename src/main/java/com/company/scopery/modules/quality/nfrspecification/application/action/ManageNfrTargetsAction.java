package com.company.scopery.modules.quality.nfrspecification.application.action;
import com.company.scopery.modules.quality.shared.activity.QualityActivityLogger;
import com.company.scopery.modules.quality.shared.authorization.QualityAuthorizationService;
import com.company.scopery.modules.quality.shared.constant.QualityActivityActions;
import com.company.scopery.modules.quality.shared.constant.QualityEntityTypes;
import com.company.scopery.modules.quality.shared.util.QualityEnumParser;
import com.company.scopery.modules.quality.nfrspecification.application.command.ManageNfrTargetsCommand;
import com.company.scopery.modules.quality.nfrspecification.application.response.NfrTargetResponse;
import com.company.scopery.modules.quality.nfrspecification.domain.enums.NfrTargetType;
import com.company.scopery.modules.quality.nfrspecification.domain.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component
public class ManageNfrTargetsAction {
    private final NfrTargetRepository repo;
    private final QualityAuthorizationService authorization;
    private final QualityActivityLogger activityLogger;
    public ManageNfrTargetsAction(NfrTargetRepository repo,
            QualityAuthorizationService authorization, QualityActivityLogger activityLogger) {
        this.repo = repo; this.authorization = authorization; this.activityLogger = activityLogger;
    }
    @Transactional
    public NfrTargetResponse.ListResponse execute(ManageNfrTargetsCommand c) {
        authorization.requireQualityUpdate(c.projectId());
        repo.deleteByRequirementId(c.requirementId());
        List<NfrTarget> newTargets = c.targets() == null ? List.of() :
                c.targets().stream().map(t -> NfrTarget.create(c.requirementId(),
                        QualityEnumParser.parseRequired(NfrTargetType.class, t.targetType(), "targetType"),
                        t.targetId(), t.targetLabel(), t.displayOrder())).toList();
        var saved = repo.saveAll(newTargets);
        activityLogger.logSuccess(QualityEntityTypes.NFR_TARGET, c.requirementId(),
                QualityActivityActions.NFR_TARGETS_MANAGED, "NFR targets replaced");
        return new NfrTargetResponse.ListResponse(c.requirementId(),
                saved.stream().map(NfrTargetResponse::from).toList());
    }
}
