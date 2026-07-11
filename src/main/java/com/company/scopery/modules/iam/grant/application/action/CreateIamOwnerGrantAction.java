package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.CreateIamOwnerGrantCommand;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateIamOwnerGrantAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamOwnerPolicyRepository ownerPolicyRepository;

    public CreateIamOwnerGrantAction(IamAccessGrantRepository grantRepository,
                                     IamAccessGrantPermissionActionRepository grantActionRepository,
                                     IamPermissionRepository permissionRepository,
                                     IamPermissionActionDefinitionRepository actionRepository,
                                     IamOwnerPolicyRepository ownerPolicyRepository) {
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.ownerPolicyRepository = ownerPolicyRepository;
    }

    @Transactional
    public void execute(CreateIamOwnerGrantCommand command) {
        try {
            IamOwnerPolicy policy = ownerPolicyRepository.findActiveByResourceType(command.resourceType())
                    .orElseThrow(() -> IamExceptions.iamOwnerPolicyNotFound(command.resourceType().name()));
            IamAccessGrant grant = grantRepository.save(
                    IamAccessGrant.createWithMetadata(IamSubjectType.USER, command.ownerUserId(), command.resourceId(),
                            null, IamGrantEffect.ALLOW, null, null, null,
                            IamGrantKind.OWNER, policy.id(), policy.canDelegate(), policy.delegationDepth(),
                            null, null, "Applied by owner policy v" + policy.policyVersion(), command.ownerUserId()));

            for (var authority : policy.actionBundle()) {
                IamPermission permission = permissionRepository.findByCode(IamPermissionCode.of(authority.permissionCode()))
                        .orElseThrow(() -> IamExceptions.iamPermissionNotFound(authority.permissionCode()));
                IamPermissionActionDefinition action = actionRepository
                        .findByPermissionIdAndActionCode(permission.id(), authority.actionCode())
                        .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(
                                authority.permissionCode(), authority.actionCode()));
                grantActionRepository.save(IamAccessGrantPermissionAction.create(grant.id(), action.id()));
            }
        } catch (Exception e) {
            throw IamExceptions.iamOwnerGrantBootstrapFailed(command.entityType(), command.entityId(), e.getMessage());
        }
    }
}
