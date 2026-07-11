package com.company.scopery.modules.iam.authorization.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.enums.AuthorizationDecisionReason;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationReadRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.enums.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.common.audit.ImmutableAuditEventService;
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

    @Mock AuthorizationReadRepository readRepository;
    @Mock IamActivityLogger activityLogger;
    @Mock ImmutableAuditEventService auditEventService;

    private AuthorizationDecisionService service;

    private final UUID USER_ID        = UUID.randomUUID();
    private final UUID RESOURCE_ID    = UUID.randomUUID();
    private final UUID RIGHT_ID       = UUID.randomUUID();
    private final UUID WORKSPACE_ID   = UUID.randomUUID();
    private final UUID TEAM_ID        = UUID.randomUUID();
    private final UUID ROLE_ID        = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AuthorizationDecisionService(readRepository, activityLogger, auditEventService);
        lenient().when(readRepository.findOrgTeamIdsByUserId(any(UUID.class))).thenReturn(List.of());
    }

    // ── Validation guards ──────────────────────────────────────────────────────

    @Test
    void canAccess_userNotFound_returnsDenyUserNotFound() {
        when(readRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_NOT_FOUND);
    }

    @Test
    void canAccess_userInactive_returnsDenyUserInactive() {
        when(readRepository.findUserById(USER_ID)).thenReturn(Optional.of(user(IamUserStatus.INACTIVE)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_INACTIVE);
    }

    @Test
    void canAccess_rightNotFound_returnsDenyRightNotFound() {
        when(readRepository.findUserById(USER_ID)).thenReturn(Optional.of(activeUser()));
        when(readRepository.findRightByCode(IamRightCode.of("VIEW"))).thenReturn(Optional.empty());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
    }

    @Test
    void canAccess_rightInactive_returnsDenyRightNotFound() {
        when(readRepository.findUserById(USER_ID)).thenReturn(Optional.of(activeUser()));
        when(readRepository.findRightByCode(IamRightCode.of("VIEW"))).thenReturn(Optional.of(right(IamRightStatus.INACTIVE)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
    }

    @Test
    void canAccess_resourceNotFound_returnsDenyResourceNotFound() {
        stubUserAndRight("VIEW");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(Optional.empty());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RESOURCE_NOT_FOUND);
    }

    @Test
    void canAccess_resourceInactive_returnsDenyResourceInactive() {
        stubUserAndRight("VIEW");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(Optional.of(resource(IamResourceStatus.INACTIVE, null, null)));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.RESOURCE_INACTIVE);
    }

    // ── Owner implicit rights ──────────────────────────────────────────────────

    @Test
    void canAccess_ownerGrantWithViewRight_returnsAllowUserGrant() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, USER_ID, null)));
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_GRANT_ALLOW);
    }

    @Test
    void canAccess_ownerGrantWithManagePermission_returnsAllowUserGrant() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("MANAGE_PERMISSION");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, USER_ID, null)));
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

        AuthorizationDecision result = service.canAccess(req("MANAGE_PERMISSION"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.USER_GRANT_ALLOW);
    }

    @Test
    void canAccess_ownerWithNonImplicitRight_proceedsToGrantCheck() {
        stubUserAndRight("PUBLISH");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, USER_ID, null)));
        stubNoGrantsNoTeamsNoRoles();

        AuthorizationDecision result = service.canAccess(req("PUBLISH"));

        // Owner implicit does not cover PUBLISH → falls through to default deny
        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    // ── Direct user grant ──────────────────────────────────────────────────────

    @Test
    void canAccess_userDirectGrantWithoutRights_returnsDefaultDeny() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    @Test
    void canAccess_userDirectSpecificRightAllowGrant_returnsAllowUser() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

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
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        // Grant has rights, but not the VIEW right
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of());

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
        when(readRepository.findWorkspaceTeamIdsByUserId(USER_ID))
                .thenReturn(List.of(TEAM_ID));
        when(readRepository.findActiveRoleAssignmentsByAssigneeId(USER_ID)).thenReturn(List.of());
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.TEAM, TEAM_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.TEAM_GRANT_ALLOW);
    }

    @Test
    void canAccess_roleGrantAllow_returnsAllowRole() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        when(readRepository.findWorkspaceTeamIdsByUserId(USER_ID)).thenReturn(List.of());
        when(readRepository.findActiveRoleAssignmentsByAssigneeId(USER_ID))
                .thenReturn(List.of(roleAssignment(ROLE_ID)));
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.ROLE, ROLE_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isTrue();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.ROLE_GRANT_ALLOW);
    }

    // ── DENY wins ─────────────────────────────────────────────────────────────

    @Test
    void canAccess_denyGrantWithSpecificRight_returnsExplicitDeny() {
        UUID grantId = UUID.randomUUID();
        stubUserAndRight("VIEW");
        stubActiveResource();
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.DENY)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

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
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(
                        grant(denyGrantId,  IamSubjectType.USER, USER_ID, IamGrantEffect.DENY),
                        grant(allowGrantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(denyGrantId, allowGrantId));

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.EXPLICIT_DENY);
    }

    // ── Visibility fallback ────────────────────────────────────────────────────

    @Test
    void canAccess_workspaceVisibilityMember_noLongerGrantsAccess_returnsDeny() {
        stubUserAndRight("VIEW");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, WORKSPACE_ID)));
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());

        AuthorizationDecision result = service.canAccess(req("VIEW"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    @Test
    void canAccess_workspaceVisibilityNonViewRight_returnsDeny() {
        stubUserAndRight("DELETE");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, WORKSPACE_ID)));
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());

        AuthorizationDecision result = service.canAccess(req("DELETE"));

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    @Test
    void canAccess_workspaceVisibilityNotMember_returnsDeny() {
        stubUserAndRight("VIEW");
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, WORKSPACE_ID)));
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());

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
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.USER, USER_ID, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

        assertThatNoException().isThrownBy(() -> service.requireAccess(req("VIEW")));
    }

    @Test
    void requireAccess_denied_throwsAccessDenied() {
        when(readRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

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
        when(readRepository.findWorkspaceTeamIdsByUserId(USER_ID)).thenReturn(List.of(TEAM_ID, team2Id));
        when(readRepository.findActiveRoleAssignmentsByAssigneeId(USER_ID)).thenReturn(List.of());
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of(grant(grantId, IamSubjectType.TEAM, team2Id, IamGrantEffect.ALLOW)));
        when(readRepository.findGrantIdsHavingRight(anyList(), eq(RIGHT_ID))).thenReturn(Set.of(grantId));

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
        return new IamUser(USER_ID, Username.of("testuser"), EmailAddress.of("test@example.com"),
                "Test User", null, status, Instant.now(), Instant.now());
    }

    private IamRight right(IamRightStatus status) {
        return new IamRight(RIGHT_ID, IamRightCode.of("VIEW"), "View", null, "IAM", status,
                Instant.now(), Instant.now());
    }

    private IamAuthResource resource(IamResourceStatus status, UUID ownerUserId, UUID workspaceId) {
        return new IamAuthResource(RESOURCE_ID,
                IamResourceCode.of("RES_001"), IamResourceType.WORKSPACE,
                "Test Resource", null, null, ownerUserId, null, workspaceId,
                workspaceId != null ? IamResourceVisibility.PRIVATE : null,
                null, status, 0, Instant.now(), Instant.now());
    }

    private IamAccessGrant grant(UUID id, IamSubjectType subjectType, UUID subjectId, IamGrantEffect effect) {
        Instant now = Instant.now();
        return new IamAccessGrant(id, subjectType, subjectId, RESOURCE_ID, null,
                effect, null, null, null,
                null, null, false, 0, null, null, null,
                IamAccessGrantStatus.ACTIVE, null, now, 0, now, now);
    }

    private IamRoleAssignment roleAssignment(UUID roleId) {
        return new IamRoleAssignment(UUID.randomUUID(), RoleAssigneeType.USER, USER_ID,
                roleId, null, null, Instant.now(),
                IamRoleAssignmentStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private void stubUserAndRight(String rightCode) {
        when(readRepository.findUserById(USER_ID)).thenReturn(Optional.of(activeUser()));
        when(readRepository.findRightByCode(IamRightCode.of(rightCode))).thenReturn(Optional.of(right(IamRightStatus.ACTIVE)));
    }

    private void stubActiveResource() {
        when(readRepository.findResourceById(RESOURCE_ID)).thenReturn(
                Optional.of(resource(IamResourceStatus.ACTIVE, null, null)));
    }

    private void stubNoTeamsNoRoles() {
        when(readRepository.findWorkspaceTeamIdsByUserId(USER_ID)).thenReturn(List.of());
        when(readRepository.findOrgTeamIdsByUserId(USER_ID)).thenReturn(List.of());
        when(readRepository.findActiveRoleAssignmentsByAssigneeId(USER_ID)).thenReturn(List.of());
    }

    private void stubNoGrantsNoTeamsNoRoles() {
        stubNoTeamsNoRoles();
        when(readRepository.findActiveGrantsBySubjectsAndResource(anyList(), anyList(), eq(RESOURCE_ID)))
                .thenReturn(List.of());
    }
}
