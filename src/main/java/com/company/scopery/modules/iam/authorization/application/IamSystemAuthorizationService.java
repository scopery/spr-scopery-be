package com.company.scopery.modules.iam.authorization.application;

import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.user.domain.IamUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Guards system-scoped operations using IAM rights.
 *
 * LIMITATION (Phase 1): The current IAM model has no role-to-right mapping table.
 * This service grants access to any user with at least one active system-scoped role
 * assignment (workspaceId = null). Fine-grained right-level checking (e.g. only roles
 * that include SYSTEM_MANAGE_NOTIFICATION_TEMPLATE) requires a role-right assignment
 * table and will be implemented in a future IAM hardening phase.
 *
 * The notification IAM rights (SYSTEM_MANAGE_NOTIFICATION_TEMPLATE etc.) are seeded
 * into the right catalog and can be used immediately once the role-right mapping table
 * is available.
 */
@Service
public class IamSystemAuthorizationService {

    private final CurrentUserAuthorizationService currentUserService;
    private final IamRoleAssignmentRepository roleAssignmentRepository;

    public IamSystemAuthorizationService(CurrentUserAuthorizationService currentUserService,
                                          IamRoleAssignmentRepository roleAssignmentRepository) {
        this.currentUserService = currentUserService;
        this.roleAssignmentRepository = roleAssignmentRepository;
    }

    /**
     * Throws 403 if the current user does not have at least one active system-scoped role.
     *
     * @param rightCode the system right code required (seeded but not yet fully enforced per-right)
     */
    public void requireSystemRight(String rightCode) {
        IamUser user = currentUserService.resolveCurrentUser();
        if (!hasSystemRight(user.id(), rightCode)) {
            throw new AppException(IamErrorCatalog.IAM_ACCESS_DENIED,
                    "Access denied: system right required [" + rightCode + "]");
        }
    }

    /**
     * Returns true if the user has at least one active system-scoped (workspaceId=null) role assignment.
     * Right-code parameter is accepted for forward compatibility but not yet checked per-right.
     */
    public boolean hasSystemRight(UUID userId, String rightCode) {
        List<IamRoleAssignment> assignments = roleAssignmentRepository.findActiveByAssigneeId(userId);
        return assignments.stream()
                .anyMatch(a -> a.status() == IamRoleAssignmentStatus.ACTIVE
                        && a.workspaceId() == null);
    }
}
