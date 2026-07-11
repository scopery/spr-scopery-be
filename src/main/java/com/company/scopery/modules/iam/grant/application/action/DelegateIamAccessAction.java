package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.command.DelegateIamAccessCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DelegateIamAccessAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final OrgTeamMemberRepository teamMemberRepository;
    private final OrgTeamRepository teamRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final IamActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public DelegateIamAccessAction(IamAccessGrantRepository grantRepository,
                                   IamAccessGrantPermissionActionRepository grantActionRepository,
                                   IamPermissionRepository permissionRepository,
                                   IamPermissionActionDefinitionRepository actionRepository,
                                   IamAuthResourceRepository resourceRepository,
                                   OrgTeamMemberRepository teamMemberRepository,
                                   OrgTeamRepository teamRepository,
                                   OrgMemberRepository orgMemberRepository,
                                   IamRoleAssignmentRepository roleAssignmentRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   IamActivityLogger activityLogger,
                                   ImmutableAuditEventService auditEventService) {
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.resourceRepository = resourceRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.currentUserService = currentUserService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public IamAccessGrantResponse execute(DelegateIamAccessCommand command) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamResourceType resourceType = IamEnumParser.parseRequired(IamResourceType.class, command.resourceType(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code(), "resourceType");
        IamSubjectType subjectType = IamEnumParser.parseRequired(IamSubjectType.class, command.subjectType(),
                IamErrorCatalog.INVALID_IAM_SUBJECT_TYPE.code(), "subjectType");
        IamAuthResource resource = resourceRepository.findByRefIdAndResourceType(command.resourceRefId(), resourceType)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(command.resourceRefId()));
        try {
            validateTenantSubject(subjectType, command.subjectId(), resource);
            List<IamPermissionActionDefinition> actions = resolveActions(command.actions());
            List<IamAccessGrant> actorGrants = actorGrants(actorId, resource);
            List<IamAccessGrant> sources = actions.stream()
                    .map(action -> findDelegableSource(actorGrants, action.id(), command.delegationDepth(), actorId, resource.id()))
                    .distinct().toList();
            if (grantRepository.existsBySubjectIdAndResourceId(command.subjectId(), resource.id())) {
                throw IamExceptions.iamAccessGrantAlreadyExists(command.subjectId(), resource.id());
            }
            UUID sourcePolicyId = sources.stream().map(IamAccessGrant::sourcePolicyId)
                    .filter(java.util.Objects::nonNull).findFirst().orElse(null);
            IamAccessGrant saved = grantRepository.save(IamAccessGrant.createWithMetadata(
                    subjectType, command.subjectId(), resource.id(), null, IamGrantEffect.ALLOW,
                    null, null, resource.workspaceId(), IamGrantKind.DELEGATED, sourcePolicyId,
                    command.delegationDepth() > 0, command.delegationDepth(), command.expiresAt(),
                    command.conditionJson(), command.reason(), actorId));
            actions.forEach(action -> grantActionRepository.save(
                    IamAccessGrantPermissionAction.create(saved.id(), action.id())));
            activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                    IamActivityActions.DELEGATE_IAM_ACCESS,
                    "Access delegated by " + actorId + " to " + command.subjectId());
            auditEventService.record(AuditEventType.ACCESS_GRANT_DELEGATED, actorId, "USER",
                    "IAM_ACCESS_GRANT", saved.id(), resource.organizationId(), resource.workspaceId(),
                    null, IamAccessGrantResponse.from(saved), command.reason());
            return IamAccessGrantResponse.from(saved);
        } catch (RuntimeException exception) {
            activityLogger.logSuccess(IamEntityTypes.IAM_AUTHORIZATION_DECISION, resource.id(),
                    IamActivityActions.REJECT_IAM_DELEGATION,
                    "Delegation rejected for actor " + actorId + ": " + exception.getMessage());
            auditEventService.record(AuditEventType.IAM_DELEGATION_REJECTED, actorId, "USER",
                    resource.resourceType().name(), resource.refId(), resource.organizationId(),
                    resource.workspaceId(), null, null, exception.getMessage());
            throw exception;
        }
    }

    private void validateTenantSubject(IamSubjectType type, UUID subjectId, IamAuthResource resource) {
        if (resource.organizationId() == null) return;
        if (type == IamSubjectType.USER
                && !orgMemberRepository.isActiveMember(resource.organizationId(), subjectId)) {
            throw IamExceptions.iamDelegationNotPermitted(subjectId, resource.id());
        }
        if (type == IamSubjectType.TEAM) {
            var team = teamRepository.findById(subjectId)
                    .orElseThrow(() -> IamExceptions.iamDelegationNotPermitted(subjectId, resource.id()));
            if (!team.organizationId().equals(resource.organizationId())) {
                throw IamExceptions.iamDelegationNotPermitted(subjectId, resource.id());
            }
        }
    }

    private List<IamPermissionActionDefinition> resolveActions(List<IamOwnerPolicyAction> requested) {
        return requested.stream().map(action -> {
            var permission = permissionRepository.findByCode(IamPermissionCode.of(action.permissionCode()))
                    .orElseThrow(() -> IamExceptions.iamPermissionNotFound(action.permissionCode()));
            return actionRepository.findByPermissionIdAndActionCode(permission.id(), action.actionCode())
                    .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(
                            action.permissionCode(), action.actionCode()));
        }).toList();
    }

    private List<IamAccessGrant> actorGrants(UUID actorId, IamAuthResource resource) {
        List<IamSubjectType> types = new ArrayList<>(List.of(IamSubjectType.USER));
        List<UUID> ids = new ArrayList<>(List.of(actorId));
        teamMemberRepository.findAllByUserId(actorId).forEach(member -> {
            types.add(IamSubjectType.TEAM); ids.add(member.teamId());
        });
        roleAssignmentRepository.findActiveByAssigneeId(actorId).forEach(assignment -> {
            if (assignment.workspaceId() == null || assignment.workspaceId().equals(resource.workspaceId())) {
                types.add(IamSubjectType.ROLE); ids.add(assignment.roleId());
            }
        });
        return grantRepository.findActiveBySubjectsAndResource(types, ids, resource.id());
    }

    private IamAccessGrant findDelegableSource(List<IamAccessGrant> grants, UUID actionId,
                                               int requestedDepth, UUID actorId, UUID resourceId) {
        return grants.stream()
                .filter(IamAccessGrant::canDelegate)
                .filter(grant -> grant.delegationDepth() > requestedDepth)
                .filter(grant -> grantActionRepository.existsByGrantIdAndPermissionActionId(grant.id(), actionId))
                .findFirst()
                .orElseThrow(() -> IamExceptions.iamDelegationNotPermitted(actorId, resourceId));
    }
}
