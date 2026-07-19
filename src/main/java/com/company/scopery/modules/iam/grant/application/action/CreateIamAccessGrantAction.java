package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
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
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
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
    private final OrgMemberRepository orgMemberRepository;
    private final OrgTeamRepository orgTeamRepository;

    public CreateIamAccessGrantAction(IamAccessGrantRepository grantRepository,
                                      IamAuthResourceRepository resourceRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      IamActivityLogger activityLogger,
                                      AuthorizationDecisionService authorizationDecisionService,
                                      ImmutableAuditEventService auditEventService,
                                      OrgMemberRepository orgMemberRepository,
                                      OrgTeamRepository orgTeamRepository) {
        this.grantRepository = grantRepository;
        this.resourceRepository = resourceRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
        this.authorizationDecisionService = authorizationDecisionService;
        this.auditEventService = auditEventService;
        this.orgMemberRepository = orgMemberRepository;
        this.orgTeamRepository = orgTeamRepository;
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

        validateTenantSubject(subjectType, command.subjectId(), resource);

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

        IamGrantKind kind = switch (subjectType) {
            case USER -> IamGrantKind.DIRECT;
            case TEAM -> IamGrantKind.TEAM;
            case ROLE -> IamGrantKind.ROLE;
        };
        IamAccessGrant grant = IamAccessGrant.createWithMetadata(
                subjectType, command.subjectId(), command.resourceId(), command.roleId(),
                effect == null ? IamGrantEffect.ALLOW : effect,
                scopeType, command.scopeRefId(), effectiveWorkspaceId,
                kind, null, false, 0, command.expiresAt(), null, null, actorId);
        IamAccessGrant saved = grantRepository.save(grant);

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.CREATE_IAM_ACCESS_GRANT,
                "Access grant created for subject " + saved.subjectId() + " on resource " + saved.resourceId());
        auditEventService.record(AuditEventType.ACCESS_GRANT_CREATED, actorId, "USER",
                "IAM_ACCESS_GRANT", saved.id(), resource.organizationId(), resource.workspaceId(),
                null, IamAccessGrantResponse.from(saved), "Access grant created");

        return IamAccessGrantResponse.from(saved);
    }

    private void validateTenantSubject(IamSubjectType type, UUID subjectId, IamAuthResource resource) {
        if (resource.organizationId() == null) {
            return;
        }
        if (type == IamSubjectType.USER
                && !orgMemberRepository.isActiveMember(resource.organizationId(), subjectId)) {
            throw IamExceptions.iamDelegationNotPermitted(subjectId, resource.id());
        }
        if (type == IamSubjectType.TEAM) {
            var team = orgTeamRepository.findById(subjectId)
                    .orElseThrow(() -> IamExceptions.iamDelegationNotPermitted(subjectId, resource.id()));
            if (!team.organizationId().equals(resource.organizationId())) {
                throw IamExceptions.iamDelegationNotPermitted(subjectId, resource.id());
            }
        }
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
