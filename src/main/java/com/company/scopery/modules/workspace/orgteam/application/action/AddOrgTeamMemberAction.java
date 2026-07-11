package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.orgteam.application.command.AddOrgTeamMemberCommand;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamMemberResponse;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMember;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AddOrgTeamMemberAction {

    private final OrgTeamRepository orgTeamRepository;
    private final OrgTeamMemberRepository orgTeamMemberRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public AddOrgTeamMemberAction(OrgTeamRepository orgTeamRepository,
                                   OrgTeamMemberRepository orgTeamMemberRepository,
                                   OrgMemberRepository orgMemberRepository,
                                   CurrentUserAuthorizationService currentUserAuthorizationService,
                                   WorkspaceIamIntegrationService iamIntegrationService,
                                   WorkspaceActivityLogger activityLogger) {
        this.orgTeamRepository = orgTeamRepository;
        this.orgTeamMemberRepository = orgTeamMemberRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgTeamMemberResponse execute(AddOrgTeamMemberCommand command) {
        OrgTeam team = orgTeamRepository.findById(command.teamId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(command.teamId()));

        if (!team.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgTeamNotFound(command.teamId());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.TEAM_MEMBER_ADD);

        if (team.status() != OrgTeamStatus.ACTIVE) {
            throw WorkspaceExceptions.orgTeamNotActive(team.code().value());
        }

        if (!orgMemberRepository.isActiveMember(command.organizationId(), command.userId())) {
            throw WorkspaceExceptions.orgTeamMemberRequiresOrgMember(command.userId(), command.organizationId());
        }

        if (orgTeamMemberRepository.existsByTeamIdAndUserId(command.teamId(), command.userId())) {
            throw WorkspaceExceptions.orgTeamMemberAlreadyExists(command.teamId(), command.userId());
        }

        OrgTeamMember member = OrgTeamMember.create(command.teamId(), command.userId());
        OrgTeamMember saved = orgTeamMemberRepository.save(member);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM_MEMBER, command.teamId(),
                WorkspaceActivityActions.ADD_ORG_TEAM_MEMBER,
                "Member added to org team: userId=" + command.userId());

        return OrgTeamMemberResponse.from(saved);
    }
}
