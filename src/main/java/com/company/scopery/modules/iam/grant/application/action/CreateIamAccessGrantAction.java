package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantScopeType;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateIamAccessGrantAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final IamActivityLogger activityLogger;
    private final AuthorizationDecisionService authorizationDecisionService;
    private final ImmutableAuditEventService auditEventService;

    public CreateIamAccessGrantAction(IamAccessGrantRepository grantRepository,
                                      IamAuthResourceRepository resourceRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      IamActivityLogger activityLogger,
                                      AuthorizationDecisionService authorizationDecisionService,
                                      ImmutableAuditEventService auditEventService) {
        this.grantRepository = grantRepository;
        this.resourceRepository = resourceRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
        this.authorizationDecisionService = authorizationDecisionService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public IamAccessGrantResponse execute(CreateIamAccessGrantCommand command) {
        IamSubjectType subjectType = IamEnumParser.parseRequired(
                IamSubjectType.class, command.subjectType(),
                IamErrorCatalog.INVALID_IAM_SUBJECT_TYPE.code(), "subjectType");

        IamAuthResource resource = resourceRepository.findById(command.resourceId())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(command.resourceId()));

        if (resource.status() != IamResourceStatus.ACTIVE) {
            throw IamExceptions.iamAuthResourceInactiveCannotBeUsed(resource.code().value());
        }

        if (grantRepository.existsBySubjectIdAndResourceId(command.subjectId(), command.resourceId())) {
            throw IamExceptions.iamAccessGrantAlreadyExists(command.subjectId(), command.resourceId());
        }

        IamGrantEffect effect = IamEnumParser.parseOptional(
                IamGrantEffect.class, command.effect(),
                IamErrorCatalog.INVALID_IAM_GRANT_EFFECT.code(), "effect");
        IamGrantScopeType scopeType = IamEnumParser.parseOptional(
                IamGrantScopeType.class, command.scopeType(),
                IamErrorCatalog.INVALID_IAM_GRANT_SCOPE_TYPE.code(), "scopeType");
        UUID effectiveWorkspaceId = resolveGrantWorkspaceId(resource, command.workspaceId());

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        IamPermissionAction manageAuthority = switch (resource.resourceType()) {
            case ORGANIZATION -> IamAuthorities.ORGANIZATION_MANAGE;
            case WORKSPACE -> IamAuthorities.WORKSPACE_ACCESS_MANAGE_ACCESS;
            case TEAM -> IamAuthorities.TEAM_MANAGE;
            default -> IamAuthorities.SYSTEM_IAM_MANAGE_ACCESS_GRANT;
        };
        authorizationDecisionService.requireAccess(new AuthorizationRequest(actorId, resource.id(), manageAuthority));

        IamAccessGrant grant = IamAccessGrant.create(subjectType, command.subjectId(),
                command.resourceId(), command.roleId(),
                effect == null ? IamGrantEffect.ALLOW : effect,
                scopeType, command.scopeRefId(), effectiveWorkspaceId, actorId);
        IamAccessGrant saved = grantRepository.save(grant);

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.CREATE_IAM_ACCESS_GRANT,
                "Access grant created for subject " + saved.subjectId() + " on resource " + saved.resourceId());
        auditEventService.record(AuditEventType.ACCESS_GRANT_CREATED, actorId, "USER",
                "IAM_ACCESS_GRANT", saved.id(), resource.organizationId(), resource.workspaceId(),
                null, IamAccessGrantResponse.from(saved), "Access grant created");

        return IamAccessGrantResponse.from(saved);
    }

    private UUID resolveGrantWorkspaceId(IamAuthResource resource, UUID requestedWorkspaceId) {
        if (requestedWorkspaceId != null) {
            return requestedWorkspaceId;
        }
        if (resource.workspaceId() != null) {
            return resource.workspaceId();
        }
        if (resource.resourceType() == IamResourceType.WORKSPACE) {
            return resource.refId();
        }
        return null;
    }
}
