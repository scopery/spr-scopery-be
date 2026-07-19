package com.company.scopery.modules.traceability.requirement.application.action;
import com.company.scopery.modules.traceability.requirement.application.command.ArchiveRequirementCommand;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class ArchiveRequirementAction {
    private final RequirementRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;
    public ArchiveRequirementAction(RequirementRepository repo, TraceabilityAuthorizationService authorization, TraceabilityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RequirementResponse execute(ArchiveRequirementCommand c) {
        authorization.requireUpdate(c.projectId());
        var req = repo.findByIdAndProjectId(c.id(), c.projectId()).orElseThrow(() -> TraceabilityExceptions.requirementNotFound(c.id()));
        var saved = repo.save(req.archive());
        activityLogger.logSuccess(TraceabilityEntityTypes.REQUIREMENT, saved.id(), TraceabilityActivityActions.REQUIREMENT_ARCHIVED, "Requirement archived");
        return RequirementResponse.from(saved);
    }
}
