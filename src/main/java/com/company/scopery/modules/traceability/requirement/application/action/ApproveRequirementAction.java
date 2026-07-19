package com.company.scopery.modules.traceability.requirement.application.action;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse; import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger; import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.*; import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class ApproveRequirementAction {
    private final RequirementRepository repo; private final TraceabilityAuthorizationService authorization; private final TraceabilityActivityLogger activityLogger;
    public ApproveRequirementAction(RequirementRepository repo, TraceabilityAuthorizationService authorization, TraceabilityActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public RequirementResponse execute(UUID projectId, UUID requirementId) {
        authorization.requireApprove(projectId);
        var req = repo.findByIdAndProjectId(requirementId, projectId).orElseThrow(() -> TraceabilityExceptions.requirementNotFound(requirementId));
        var saved = repo.save(req.approve());
        activityLogger.logSuccess(TraceabilityEntityTypes.REQUIREMENT, saved.id(), TraceabilityActivityActions.REQUIREMENT_APPROVED, "Requirement approved");
        return RequirementResponse.from(saved);
    }
}
