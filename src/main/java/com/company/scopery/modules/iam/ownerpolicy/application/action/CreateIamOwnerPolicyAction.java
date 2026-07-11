package com.company.scopery.modules.iam.ownerpolicy.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.ownerpolicy.application.command.CreateIamOwnerPolicyCommand;
import com.company.scopery.modules.iam.ownerpolicy.application.response.IamOwnerPolicyResponse;
import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamInheritanceScope;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateIamOwnerPolicyAction {
    private final IamOwnerPolicyRepository repository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;

    public CreateIamOwnerPolicyAction(IamOwnerPolicyRepository repository,
                                      IamPermissionRepository permissionRepository,
                                      IamPermissionActionDefinitionRepository actionRepository,
                                      IamSystemAuthorizationService systemAuthorizationService,
                                      IamActivityLogger activityLogger) {
        this.repository = repository; this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository; this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamOwnerPolicyResponse execute(CreateIamOwnerPolicyCommand command) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_GOVERNANCE_MANAGE.legacyRightCode());

        IamResourceType resourceType = IamEnumParser.parseRequired(IamResourceType.class, command.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        IamInheritanceScope scope = IamEnumParser.parseRequired(IamInheritanceScope.class, command.inheritanceScope(),
                IamErrorCatalog.INVALID_IAM_GRANT_SCOPE_TYPE.code(), "inheritanceScope");
        validateActions(command.actions());
        int nextVersion = repository.findAll().stream()
                .filter(policy -> policy.resourceType() == resourceType)
                .mapToInt(IamOwnerPolicy::policyVersion).max().orElse(0) + 1;
        repository.findActiveByResourceType(resourceType).ifPresent(active -> repository.save(active.supersede()));
        IamOwnerPolicy saved = repository.save(IamOwnerPolicy.create(
                resourceType, nextVersion, command.actions(), scope, command.canDelegate(), command.delegationDepth()));
        activityLogger.logSuccess(IamEntityTypes.IAM_OWNER_POLICY, saved.id(),
                IamActivityActions.CREATE_OWNER_POLICY,
                "Owner policy activated for " + resourceType + " v" + nextVersion);
        return IamOwnerPolicyResponse.from(saved);
    }

    private void validateActions(List<IamOwnerPolicyAction> actions) {
        for (IamOwnerPolicyAction requested : actions) {
            var permission = permissionRepository.findByCode(IamPermissionCode.of(requested.permissionCode()))
                    .orElseThrow(() -> IamExceptions.iamPermissionNotFound(requested.permissionCode()));
            actionRepository.findByPermissionIdAndActionCode(permission.id(), requested.actionCode())
                    .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(
                            requested.permissionCode(), requested.actionCode()));
        }
    }
}
