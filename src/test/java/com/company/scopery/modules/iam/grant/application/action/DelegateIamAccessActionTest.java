package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.command.DelegateIamAccessCommand;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DelegateIamAccessActionTest {

    @Mock private IamAccessGrantRepository grantRepository;
    @Mock private IamAccessGrantPermissionActionRepository grantActionRepository;
    @Mock private IamPermissionRepository permissionRepository;
    @Mock private IamPermissionActionDefinitionRepository actionRepository;
    @Mock private IamAuthResourceRepository resourceRepository;
    @Mock private OrgTeamMemberRepository teamMemberRepository;
    @Mock private OrgTeamRepository teamRepository;
    @Mock private OrgMemberRepository orgMemberRepository;
    @Mock private IamRoleAssignmentRepository roleAssignmentRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private IamActivityLogger activityLogger;
    @Mock private ImmutableAuditEventService auditEventService;
    @Mock private IamPermission permission;
    @Mock private IamPermissionActionDefinition actionDef;

    private DelegateIamAccessAction action;

    private UUID actorId;
    private UUID subjectId;
    private UUID orgId;
    private UUID workspaceId;
    private UUID actionId;
    private IamAuthResource resource;

    @BeforeEach
    void setUp() {
        action = new DelegateIamAccessAction(
                grantRepository, grantActionRepository, permissionRepository, actionRepository,
                resourceRepository, teamMemberRepository, teamRepository, orgMemberRepository,
                roleAssignmentRepository, currentUserService, activityLogger, auditEventService);

        actorId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        orgId = UUID.randomUUID();
        workspaceId = UUID.randomUUID();
        actionId = UUID.randomUUID();

        resource = IamAuthResource.createWithOwnership(
                IamResourceCode.of("WS_DELEGATE"), IamResourceType.WORKSPACE, "WS", null,
                workspaceId, actorId, orgId, workspaceId, IamResourceVisibility.PRIVATE, null);

        IamUser actor = IamUser.of(actorId, Username.of("actor"), EmailAddress.of("actor@example.com"),
                "Actor", null, IamUserStatus.ACTIVE, Instant.now(), Instant.now());
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(actor);
        lenient().when(resourceRepository.findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE))
                .thenReturn(Optional.of(resource));
        lenient().when(orgMemberRepository.isActiveMember(orgId, subjectId)).thenReturn(true);
        lenient().when(teamMemberRepository.findAllByUserId(actorId)).thenReturn(List.of());
        lenient().when(roleAssignmentRepository.findActiveByAssigneeId(actorId)).thenReturn(List.of());
        lenient().when(permission.id()).thenReturn(UUID.randomUUID());
        lenient().when(actionDef.id()).thenReturn(actionId);
        lenient().when(permissionRepository.findByCode(IamPermissionCode.of("WORKSPACE_MANAGEMENT")))
                .thenReturn(Optional.of(permission));
        lenient().when(actionRepository.findByPermissionIdAndActionCode(permission.id(), "MANAGE"))
                .thenReturn(Optional.of(actionDef));
    }

    @Test
    void delegateAccess_withoutCanDelegate_forbidden() {
        IamAccessGrant source = sourceGrant(false, 2);
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(resource.id())))
                .thenReturn(List.of(source));

        assertThatThrownBy(() -> action.execute(command(0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_DELEGATION_NOT_PERMITTED.code()));
        verify(grantRepository, never()).save(any());
    }

    @Test
    void delegateAccess_depthExceeded_forbidden() {
        // Source depth must be strictly greater than requested depth.
        IamAccessGrant source = sourceGrant(true, 1);
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(resource.id())))
                .thenReturn(List.of(source));

        assertThatThrownBy(() -> action.execute(command(1)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_DELEGATION_NOT_PERMITTED.code()));
        verify(grantRepository, never()).save(any());
    }

    @Test
    void delegateAccess_actionNotCovered_forbidden() {
        IamAccessGrant source = sourceGrant(true, 2);
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(resource.id())))
                .thenReturn(List.of(source));
        when(grantActionRepository.existsByGrantIdAndPermissionActionId(source.id(), actionId)).thenReturn(false);

        assertThatThrownBy(() -> action.execute(command(0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_DELEGATION_NOT_PERMITTED.code()));
        verify(grantRepository, never()).save(any());
    }

    @Test
    void delegateAccess_crossOrgRejected() {
        when(orgMemberRepository.isActiveMember(orgId, subjectId)).thenReturn(false);

        assertThatThrownBy(() -> action.execute(command(0)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_DELEGATION_NOT_PERMITTED.code()));
        verify(grantRepository, never()).save(any());
    }

    private DelegateIamAccessCommand command(int depth) {
        return new DelegateIamAccessCommand(
                "USER", subjectId, "WORKSPACE", workspaceId, depth, null, null, "test",
                List.of(new IamOwnerPolicyAction("WORKSPACE_MANAGEMENT", "MANAGE")));
    }

    private IamAccessGrant sourceGrant(boolean canDelegate, int depth) {
        Instant now = Instant.now();
        return new IamAccessGrant(
                UUID.randomUUID(), IamSubjectType.USER, actorId, resource.id(), null,
                IamGrantEffect.ALLOW, null, null, workspaceId,
                IamGrantKind.OWNER, null, canDelegate, depth, null, null, null,
                IamAccessGrantStatus.ACTIVE, actorId, now, 0, now, now);
    }
}
