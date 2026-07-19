package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.orgteam.application.command.AssignOrgTeamToWorkspaceCommand;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignmentRepository;
import com.company.scopery.modules.workspace.orgteam.domain.valueobject.OrgTeamCode;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignOrgTeamToWorkspaceActionTest {

    @Mock private OrgTeamRepository orgTeamRepository;
    @Mock private OrgTeamWorkspaceAssignmentRepository assignmentRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;
    @Mock private WorkspaceActivityLogger activityLogger;

    private AssignOrgTeamToWorkspaceAction action;
    private UUID orgA;
    private UUID orgB;
    private UUID teamId;
    private UUID workspaceId;

    @BeforeEach
    void setUp() {
        action = new AssignOrgTeamToWorkspaceAction(
                orgTeamRepository, assignmentRepository, currentUserService,
                iamIntegrationService, workspaceRepository, activityLogger);
        orgA = UUID.randomUUID();
        orgB = UUID.randomUUID();
        teamId = UUID.randomUUID();
        workspaceId = UUID.randomUUID();
        IamUser actor = IamUser.of(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("a@example.com"), "Admin", null, IamUserStatus.ACTIVE,
                Instant.now(), Instant.now());
        when(currentUserService.resolveCurrentUser()).thenReturn(actor);
    }

    @Test
    void assignOrgTeam_crossOrgRejected() {
        OrgTeam team = new OrgTeam(teamId, orgA, OrgTeamCode.of("ENG"), "Eng", null,
                com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus.ACTIVE,
                0, Instant.now(), Instant.now());
        Workspace workspace = new Workspace(workspaceId, orgB, WorkspaceCode.of("WSB"), "WS B",
                null, null, WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY,
                WorkspaceStatus.ACTIVE, 0, Instant.now(), Instant.now());
        when(orgTeamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

        assertThatThrownBy(() -> action.execute(
                new AssignOrgTeamToWorkspaceCommand(orgA, teamId, workspaceId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(WorkspaceErrorCatalog.ORG_TEAM_CROSS_ORGANIZATION_ASSIGNMENT.code()));
    }
}
