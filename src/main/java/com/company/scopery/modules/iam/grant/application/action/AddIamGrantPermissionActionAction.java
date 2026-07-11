package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantPermissionActionCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantPermissionActionResponse;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class AddIamGrantPermissionActionAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamRightRepository rightRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final AuthorizationDecisionService authorizationDecisionService;
    private final IamActivityLogger activityLogger;

    public AddIamGrantPermissionActionAction(
            IamAccessGrantRepository grantRepository,
            IamAccessGrantPermissionActionRepository grantActionRepository,
            IamAuthResourceRepository resourceRepository,
            IamPermissionRepository permissionRepository,
            IamPermissionActionDefinitionRepository actionRepository,
            IamRightRepository rightRepository,
            CurrentUserAuthorizationService currentUserAuthorizationService,
            AuthorizationDecisionService authorizationDecisionService,
            IamActivityLogger activityLogger) {
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.resourceRepository = resourceRepository;
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.rightRepository = rightRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.authorizationDecisionService = authorizationDecisionService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamAccessGrantPermissionActionResponse execute(AddIamGrantPermissionActionCommand command) {
        IamAccessGrant grant = grantRepository.findById(command.grantId())
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(command.grantId()));
        if (grant.status() == IamAccessGrantStatus.REVOKED) {
            throw IamExceptions.iamAccessGrantRevokedCannotBeModified(grant.id());
        }

        IamAuthResource resource = resourceRepository.findById(grant.resourceId())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(grant.resourceId()));

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        authorizationDecisionService.requireAccess(
                new AuthorizationRequest(actorId, resource.id(), manageAuthorityFor(resource)));

        ResolvedPermissionAction resolved = resolveAction(command.permissionActionId(),
                command.permissionCode(), command.actionCode());
        validateGrantScope(resolved.permission(), resource, grant);
        validateAssignableSubjectType(resolved.permission(), grant);
        validateGrantable(resolved.permission(), resolved.action());

        if (grantActionRepository.existsByGrantIdAndPermissionActionId(command.grantId(), resolved.action().id())) {
            throw IamExceptions.iamAccessGrantPermissionActionAlreadyExists(command.grantId(), resolved.action().id());
        }

        IamAccessGrantPermissionAction saved = grantActionRepository.save(
                IamAccessGrantPermissionAction.create(command.grantId(), resolved.action().id()));

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, grant.id(),
                IamActivityActions.ADD_IAM_GRANT_PERMISSION_ACTION,
                "Permission action " + resolved.permission().code().value() + "."
                        + resolved.action().actionCode() + " added to grant " + grant.id());

        return toResponse(grant, resolved.action(), saved);
    }

    private ResolvedPermissionAction resolveAction(UUID permissionActionId,
                                                   String permissionCode,
                                                   String actionCode) {
        if (permissionActionId != null) {
            IamPermissionActionDefinition action = actionRepository.findById(permissionActionId)
                    .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(permissionActionId));
            IamPermission permission = permissionRepository.findById(action.permissionId())
                    .orElseThrow(() -> IamExceptions.iamPermissionNotFound(action.permissionId()));
            return new ResolvedPermissionAction(permission, action);
        }

        if (permissionCode == null || permissionCode.isBlank()
                || actionCode == null || actionCode.isBlank()) {
            throw new ValidationException(
                    "permissionActionId or both permissionCode and actionCode are required");
        }

        IamPermission permission = permissionRepository.findByCode(parsePermissionCode(permissionCode))
                .orElseThrow(() -> IamExceptions.iamPermissionNotFound(permissionCode.trim().toUpperCase()));
        String normalizedActionCode = normalizeActionCode(actionCode);
        IamPermissionActionDefinition action = actionRepository
                .findByPermissionIdAndActionCode(permission.id(), normalizedActionCode)
                .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(
                        permission.code().value(), normalizedActionCode));
        return new ResolvedPermissionAction(permission, action);
    }

    private IamPermissionCode parsePermissionCode(String permissionCode) {
        try {
            return IamPermissionCode.of(permissionCode);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid permissionCode: " + permissionCode);
        }
    }

    private String normalizeActionCode(String actionCode) {
        String normalized = actionCode.trim().toUpperCase();
        if (!normalized.matches("[A-Z0-9_]+")) {
            throw new ValidationException("Invalid actionCode: " + actionCode);
        }
        return normalized;
    }

    private void validateGrantable(IamPermission permission, IamPermissionActionDefinition action) {
        if (permission.status() != IamPermissionStatus.ACTIVE) {
            throw IamExceptions.iamPermissionInactiveCannotBeUsed(permission.code().value());
        }
        if (action.status() != IamPermissionStatus.ACTIVE) {
            throw IamExceptions.iamPermissionActionInactiveCannotBeUsed(
                    permission.code().value(), action.actionCode());
        }
    }

    private void validateGrantScope(IamPermission permission, IamAuthResource resource, IamAccessGrant grant) {
        boolean valid = switch (permission.resourceScopeLevel()) {
            case SYSTEM -> resource.resourceType() == IamResourceType.GLOBAL
                    && resource.refId() == null
                    && resource.workspaceId() == null
                    && grant.workspaceId() == null;
            case ORGANIZATION -> resource.resourceType() == IamResourceType.ORGANIZATION
                    && resource.refId() != null
                    && grant.workspaceId() == null;
            case WORKSPACE -> resource.resourceType() == IamResourceType.WORKSPACE
                    && resource.refId() != null
                    && grant.workspaceId() != null
                    && grant.workspaceId().equals(resource.refId())
                    && (resource.workspaceId() == null || resource.workspaceId().equals(resource.refId()));
        };
        if (!valid) {
            throw IamExceptions.iamPermissionActionResourceScopeMismatch(
                    permission.code().value(),
                    permission.resourceScopeLevel().name(),
                    resource.resourceType().name(),
                    resource.id());
        }
    }

    private void validateAssignableSubjectType(IamPermission permission, IamAccessGrant grant) {
        IamPermissionAssignableSubjectType subjectType = IamEnumParser.parseRequired(
                IamPermissionAssignableSubjectType.class, grant.subjectType().name(),
                IamErrorCatalog.INVALID_IAM_PERMISSION_ASSIGNABLE_SUBJECT_TYPE.code(), "subjectType");
        if (!permission.assignableSubjectTypes().contains(subjectType)) {
            throw IamExceptions.iamPermissionActionSubjectTypeNotAllowed(
                    permission.code().value(), grant.subjectType().name(), grant.id());
        }
    }

    private IamAccessGrantPermissionActionResponse toResponse(IamAccessGrant grant,
                                                              IamPermissionActionDefinition action,
                                                              IamAccessGrantPermissionAction grantAction) {
        IamPermission permission = permissionRepository.findById(action.permissionId())
                .orElseThrow(() -> IamExceptions.iamPermissionNotFound(action.permissionId()));
        String legacyRightCode = Optional.ofNullable(action.rightId())
                .flatMap(rightRepository::findById)
                .map(right -> right.code().value())
                .orElse(null);
        return new IamAccessGrantPermissionActionResponse(
                grant.id(),
                grant.resourceId(),
                grant.workspaceId(),
                action.id(),
                permission.id(),
                permission.code().value(),
                action.actionCode(),
                action.rightId(),
                legacyRightCode,
                grantAction.createdAt());
    }

    private com.company.scopery.modules.iam.shared.constant.IamPermissionAction manageAuthorityFor(IamAuthResource resource) {
        return switch (resource.resourceType()) {
            case ORGANIZATION -> IamAuthorities.ORGANIZATION_MANAGE;
            case WORKSPACE -> IamAuthorities.WORKSPACE_ACCESS_MANAGE_ACCESS;
            case TEAM -> IamAuthorities.TEAM_MANAGE;
            default -> IamAuthorities.SYSTEM_IAM_MANAGE_ACCESS_GRANT;
        };
    }

    private record ResolvedPermissionAction(
            IamPermission permission,
            IamPermissionActionDefinition action) {
    }
}
