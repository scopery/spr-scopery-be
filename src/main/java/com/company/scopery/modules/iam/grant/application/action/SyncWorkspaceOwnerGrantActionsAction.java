package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Attaches missing active WORKSPACE owner-policy actions onto existing OWNER grants
 * (idempotent). Used after owner-policy catalog version bumps.
 */
@Component
public class SyncWorkspaceOwnerGrantActionsAction {

    private static final Logger log = LoggerFactory.getLogger(SyncWorkspaceOwnerGrantActionsAction.class);

    private final IamOwnerPolicyRepository ownerPolicyRepository;
    private final IamAuthResourceRepository authResourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;

    public SyncWorkspaceOwnerGrantActionsAction(
            IamOwnerPolicyRepository ownerPolicyRepository,
            IamAuthResourceRepository authResourceRepository,
            IamAccessGrantRepository grantRepository,
            IamAccessGrantPermissionActionRepository grantActionRepository,
            IamPermissionRepository permissionRepository,
            IamPermissionActionDefinitionRepository actionRepository) {
        this.ownerPolicyRepository = ownerPolicyRepository;
        this.authResourceRepository = authResourceRepository;
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public int executeAll() {
        IamOwnerPolicy policy = ownerPolicyRepository
                .findActiveByResourceType(IamResourceType.WORKSPACE)
                .orElse(null);
        if (policy == null) {
            return 0;
        }
        int attached = 0;
        for (IamAuthResource workspaceResource : authResourceRepository
                .findAllByResourceTypeAndStatus(IamResourceType.WORKSPACE, IamResourceStatus.ACTIVE)) {
            List<IamAccessGrant> grants = grantRepository.findActiveByResource(workspaceResource.id());
            for (IamAccessGrant grant : grants) {
                if (grant.kind() != IamGrantKind.OWNER) {
                    continue;
                }
                attached += attachMissing(grant.id(), policy.actionBundle());
            }
        }
        if (attached > 0) {
            log.info("[SyncWorkspaceOwnerGrant] Attached {} missing owner-policy actions", attached);
        }
        return attached;
    }

    private int attachMissing(UUID grantId, List<IamOwnerPolicyAction> actions) {
        int count = 0;
        for (IamOwnerPolicyAction authority : actions) {
            IamPermission permission = permissionRepository
                    .findByCode(IamPermissionCode.of(authority.permissionCode()))
                    .orElse(null);
            if (permission == null) {
                log.warn("Skip missing permission {}", authority.permissionCode());
                continue;
            }
            IamPermissionActionDefinition action = actionRepository
                    .findByPermissionIdAndActionCode(permission.id(), authority.actionCode())
                    .orElse(null);
            if (action == null) {
                log.warn("Skip missing action {} on {}", authority.actionCode(), authority.permissionCode());
                continue;
            }
            if (grantActionRepository.existsByGrantIdAndPermissionActionId(grantId, action.id())) {
                continue;
            }
            grantActionRepository.save(IamAccessGrantPermissionAction.create(grantId, action.id()));
            count++;
        }
        return count;
    }
}
