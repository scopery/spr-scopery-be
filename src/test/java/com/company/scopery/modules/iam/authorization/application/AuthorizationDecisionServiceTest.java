package com.company.scopery.modules.iam.authorization.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationDecisionReason;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.IamResourceVisibility;
import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightCode;
import com.company.scopery.modules.iam.right.domain.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.IamRightStatus;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.RoleAssigneeType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.team.domain.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationDecisionServiceTest {

    @Mock IamUserRepository userRepository;
    @Mock IamRightRepository rightRepository;
    @Mock IamAuthResourceRepository resourceRepository;
    @Mock IamAccessGrantRepository grantRepository;
    @Mock IamAccessGrantRightRepository grantRightRepository;
    @Mock IamRoleAssignmentRepository roleAssignmentRepository;
    @Mock TeamMemberRepository teamMemberRepository;
    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @Mock IamActivityLogger activityLogger;

    private AuthorizationDecisionService service;

    private final UUID USER_ID        = UUID.randomUUID();
    private final UUID RESOURCE_ID    = UUID.randomUUID();
    private final UUID RIGHT_ID       = UUID.randomUUID();
    private final UUID WORKSPACE_ID   = UUID.randomUUID();
    private final UUID TEAM_ID        = UUID.randomUUID();
    private final UUID ROLE_ID        = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AuthorizationDecisionService(
                userRepository, rightRepository, resourceRepository,
                grantRepository, grantRightRepository,
                roleAssignmentRepository, teamMemberRepository,
                workspaceMemberRepository, activityLogger);
    }

    // ── Validation guards ──────────────────────────────────────────────────────

    @Test
    void canAccess_userNotFound_returnsDenyUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_NOT_FOUND);
    }

    @Test
    void canAccess_userInactive_returnsDenyUserInactive() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user(IamUserStatus.INACTIVE)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_INACTIVE);
    }

    @Test
    void canAccess_rightNotFound_returnsDenyRightNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(activeUser()));
        when(rightRepository.findByCode(new IamRightCode("VIEW"))).thenReturn(Optional.empty());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
    }

    @Test
    void canAccess_rightInactive_returnsDenyRightNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(activeUser()));
        when(rightRepository.findByCode(new IamRightCode("VIEW"))).thenReturn(Optional.of(right(IamRightStatus.INACTIVE)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
    }

    @Test
    void canAccess_resourceNotFound_returnsDenyResourceNotFound() {
        stubUserAndRight("VIEW");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RESOURCE_NOT_FOUND);
    }

    @Test
    void canAccess_resourceInactive_returnsDenyResourceInactive() {
        stubUserAndRight("VIEW");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource(IamResourceStatus.INACTIVE, null, null)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RESOURCE_INACTIVE);
    }

    // ── Owner implicit rights ──────────────────────────────────────────────────

    @Test
    void canAccess_ownerWithViewRight_returnsAllowOwner() {
        stubUserAndRight("VIEW");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, USER_ID, null)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.OWNER_IMPLICIT_ALLOW);
    }

    @Test
    void canAccess_ownerWithManagePermission_returnsAllowOwner() {
        stubUserAndRight("MANAGE_PERMISSION");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, USER_ID, null)));

        AuthorizationDecision result = service.canAccess(req("MANAGE_PERMISSION"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.OWNER_IMPLICIT_ALLOW);
    }

    @Test
    void canAccess_ownerWithNonImplicitRight_proceedsToGrantCheck() {
        stubUserAndRight("PUBLISH");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, USER_ID, null)));
        stubNoGrantsNoTeamsNoRoles();

        AuthorizationDecision result = service.canAccess(req("PUBLISH"));

        // Owner implicit does not cover PUBLISH → falls through to default deny
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    // ── Direct user grant ──────────────────────────────────────────────────────

    @Test
    void canAccess_userDirectBlanketAllowGrant_returnsAllowUser() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        // Blanket grant: has no rights attached
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_GRANT_ALLOW);
    }

    @Test
    void canAccess_userDirectSpecificRightAllowGrant_returnsAllowUser() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of(grantId));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_GRANT_ALLOW);
    }

    @Test
    void canAccess_grantWithDifferentRight_doesNotAllow() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        // Grant has rights, but not the VIEW right
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of(grantId));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    // ── Team and role grants ───────────────────────────────────────────────────

    @Test
    void canAccess_teamGrantAllow_returnsAllowTeam() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        when(teamMemberRepository.findByUserId(USER_ID))
                .thenReturn(List.of(new WorkspaceTeamMember(TEAM_ID, USER_ID, Instant.now())));
        when(roleAssignmentRepository.findActiveByAssigneeId(USER_ID)).thenReturn(List.of());
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.TEAM, TEAM_ID, IamGrantEffect.ALLOW)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.TEAM_GRANT_ALLOW);
    }

    @Test
    void canAccess_roleGrantAllow_returnsAllowRole() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        when(teamMemberRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(roleAssignmentRepository.findActiveByAssigneeId(USER_ID))
                .thenReturn(List.of(roleAssignment(ROLE_ID)));
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.ROLE, ROLE_ID, IamGrantEffect.ALLOW)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.ROLE_GRANT_ALLOW);
    }

    // ── DENY wins ─────────────────────────────────────────────────────────────

    @Test
    void canAccess_denyGrantBlanket_returnsExplicitDeny() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.DENY)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.EXPLICIT_DENY);
    }

    @Test
    void canAccess_denyAndAllowGrant_denyWins() {
        UUID denyGrantId  = UUID.randomUUID();
        UUID allowGrantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(
                        grant(denyGrantId,  IamSubjectType.USER, USER_ID, IamGrantEffect.DENY),
                        grant(allowGrantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.EXPLICIT_DENY);
    }

    // ── Visibility fallback ────────────────────────────────────────────────────

    @Test
    void canAccess_workspaceVisibilityMember_returnsAllowVisibility() {
        stubUserAndRight("VIEW");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, WORKSPACE_ID)));
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());
        when(workspaceMemberRepository.isActiveMember(WORKSPACE_ID, USER_ID)).thenReturn(true);

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.VISIBILITY_ALLOW);
    }

    @Test
    void canAccess_workspaceVisibilityNonViewRight_returnsDeny() {
        stubUserAndRight("DELETE");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, WORKSPACE_ID)));
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());

        AuthorizationDecision result = service.canAccess(req("DELETE"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    @Test
    void canAccess_workspaceVisibilityNotMember_returnsDeny() {
        stubUserAndRight("VIEW");
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, WORKSPACE_ID)));
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());
        when(workspaceMemberRepository.isActiveMember(WORKSPACE_ID, USER_ID)).thenReturn(false);

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    @Test
    void canAccess_noGrantsNoVisibility_returnsDenyDefault() {
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoGrantsNoTeamsNoRoles();

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    // ── requireAccess ──────────────────────────────────────────────────────────

    @Test
    void requireAccess_allowed_doesNotThrow() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        assertThatNoException().isThrownBy(() -> service.requireAccess(req("VIEW")));
    }

    @Test
    void requireAccess_denied_throwsAccessDenied() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.requireAccess(req("VIEW")))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException appEx = (AppException) ex;
                    assertThat(appEx.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(appEx.getErrorCode()).isEqualTo(IamErrorCatalog.IAM_ACCESS_DENIED.code());
                });
    }

    // ── Multiple teams ─────────────────────────────────────────────────────────

    @Test
    void canAccess_multipleTeams_anyTeamGrantAllows() {
        UUID team2Id  = UUID.randomUUID();
        UUID grantId  = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        when(teamMemberRepository.findByUserId(USER_ID)).thenReturn(List.of(
                new WorkspaceTeamMember(TEAM_ID, USER_ID, Instant.now()),
                new WorkspaceTeamMember(team2Id, USER_ID, Instant.now())));
        when(roleAssignmentRepository.findActiveByAssigneeId(USER_ID)).thenReturn(List.of());
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.TEAM, team2Id, IamGrantEffect.ALLOW)));
        when(grantRightRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());
        when(grantRightRepository.findGrantIdsHavingAnyRight(anyList())).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.TEAM_GRANT_ALLOW);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private AuthorizationRequest req(String rightCode) {
        return new AuthorizationRequest(USER_ID, RESOURCE_ID, rightCode);
    }

    private IamUser activeUser() { return user(IamUserStatus.ACTIVE); }

    private IamUser user(IamUserStatus status) {
        return new IamUser(USER_ID, new Username("testuser"), new EmailAddress("test@example.com"),
                "Test User", null, status, Instant.now(), Instant.now());
    }

    private IamRight right(IamRightStatus status) {
        return new IamRight(RIGHT_ID, new IamRightCode("VIEW"), "View", null, "IAM", status,
                Instant.now(), Instant.now());
    }

    private IamAuthResource resource(IamResourceStatus status, UUID ownerUserId, UUID workspaceId) {
        return new IamAuthResource(RESOURCE_ID,
                new IamResourceCode("RES_001"), IamResourceType.WORKSPACE,
                "Test Resource", null, null, ownerUserId, workspaceId,
                workspaceId != null ? IamResourceVisibility.WORKSPACE : null,
                null, status, Instant.now(), Instant.now());
    }

    private IamAccessGrant grant(UUID id, IamSubjectType subjectType, UUID subjectId, IamGrantEffect effect) {
        return new IamAccessGrant(id, subjectType, subjectId, RESOURCE_ID, null,
                effect, null, null, null,
                IamAccessGrantStatus.ACTIVE, null, Instant.now(), Instant.now(), Instant.now());
    }

    private IamRoleAssignment roleAssignment(UUID roleId) {
        return new IamRoleAssignment(UUID.randomUUID(), RoleAssigneeType.USER, USER_ID,
                roleId, null, null, Instant.now(),
                IamRoleAssignmentStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private void stubUserAndRight(String rightCode) {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(activeUser()));
        when(rightRepository.findByCode(new IamRightCode(rightCode))).thenReturn(Optional.of(right(IamRightStatus.ACTIVE)));
    }

    private void stubActiveResource() {
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, null)));
    }

    private void stubNoTeamsNoRoles() {
        when(teamMemberRepository.findByUserId(USER_ID)).thenReturn(List.of());
        when(roleAssignmentRepository.findActiveByAssigneeId(USER_ID)).thenReturn(List.of());
    }

    private void stubNoGrantsNoTeamsNoRoles() {
        stubNoTeamsNoRoles();
        when(grantRepository.findActiveBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());
    }
}
