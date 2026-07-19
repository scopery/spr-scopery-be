package com.company.scopery.modules.resourcecapacity.conflict.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.resourcecapacity.conflict.application.response.AssignmentConflictApiResponse;
import com.company.scopery.modules.resourcecapacity.conflict.domain.model.AssignmentConflictRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AcknowledgeAssignmentConflictAction {
    private final AssignmentConflictRepository repo;
    private final CapacityWorkspaceAuthorizationService auth;
    private final CapacityActivityLogger activity;

    public AcknowledgeAssignmentConflictAction(
            AssignmentConflictRepository repo,
            CapacityWorkspaceAuthorizationService auth,
            CapacityActivityLogger activity) {
        this.repo = repo;
        this.auth = auth;
        this.activity = activity;
    }

    @Transactional
    public AssignmentConflictApiResponse execute(UUID projectId, UUID conflictId) {
        return execute(projectId, conflictId, null);
    }

    @Transactional
    public AssignmentConflictApiResponse execute(UUID projectId, UUID conflictId, String notes) {
        var c = repo.findById(conflictId).orElseThrow(() -> CapacityExceptions.conflictNotFound(conflictId));
        if (c.projectId() == null || !c.projectId().equals(projectId)) {
            throw CapacityExceptions.conflictNotFound(conflictId);
        }
        auth.requireWorkspacePermission(c.workspaceId(), IamAuthorities.CAPACITY_VIEW);
        var saved = repo.save(c.acknowledge());
        activity.logSuccess(
                CapacityEntityTypes.ASSIGNMENT_CONFLICT,
                saved.id(),
                CapacityActivityActions.ASSIGNMENT_CONFLICT_ACKNOWLEDGED,
                notes == null ? saved.conflictType() : notes);
        return AssignmentConflictApiResponse.from(saved);
    }
}
