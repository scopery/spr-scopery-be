package com.company.scopery.modules.workspace.orginvitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.invitation.domain.valueobject.InvitationCodeHasher;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.valueobject.OrganizationCode;
import com.company.scopery.modules.workspace.orginvitation.application.command.AcceptOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.command.CreateOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgInvitationActionTest {

    @Mock private OrgInvitationRepository invitationRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private OrgMemberRepository orgMemberRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;
    @Mock private WorkspaceActivityLogger activityLogger;

    private CreateOrgInvitationAction createAction;
    private AcceptOrgInvitationAction acceptAction;
    private UUID orgId;
    private IamUser actor;

    @BeforeEach
    void setUp() {
        createAction = new CreateOrgInvitationAction(
                invitationRepository, organizationRepository, currentUserService, iamIntegrationService, activityLogger);
        acceptAction = new AcceptOrgInvitationAction(
                invitationRepository, orgMemberRepository, currentUserService, activityLogger);
        orgId = UUID.randomUUID();
        actor = IamUser.of(UUID.randomUUID(), Username.of("owner"), EmailAddress.of("o@example.com"),
                "Owner", null, IamUserStatus.ACTIVE, Instant.now(), Instant.now());
        when(currentUserService.resolveCurrentUser()).thenReturn(actor);
    }

    @Test
    void createOrgInvitation_storesHashAndReturnsRawTokenOnce() {
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(
                new Organization(orgId, OrganizationCode.of("ACME"), "Acme", null, actor.id(),
                        OrganizationStatus.ACTIVE, 0, Instant.now(), Instant.now())));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrgInvitationResponse response = createAction.execute(
                new CreateOrgInvitationCommand(orgId, "invitee@example.com", "MEMBER", null));

        assertThat(response.token()).isNotBlank();
        ArgumentCaptor<OrgInvitation> captor = ArgumentCaptor.forClass(OrgInvitation.class);
        verify(invitationRepository).save(captor.capture());
        OrgInvitation saved = captor.getValue();
        assertThat(saved.tokenHash()).isEqualTo(InvitationCodeHasher.hash(response.token()));
        assertThat(saved.tokenHash()).isNotEqualTo(response.token());
        assertThat(saved.tokenHint()).isEqualTo(InvitationCodeHasher.hint(response.token()));
    }

    @Test
    void acceptOrgInvitation_looksUpByHash() {
        String raw = InvitationCodeHasher.generateRawCode();
        OrgInvitation pending = OrgInvitation.create(orgId, "invitee@example.com",
                com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType.MEMBER,
                actor.id(), raw, Instant.now().plusSeconds(3600));
        when(invitationRepository.findByTokenHash(InvitationCodeHasher.hash(raw))).thenReturn(Optional.of(pending));
        when(orgMemberRepository.existsByOrganizationIdAndUserId(orgId, actor.id())).thenReturn(false);
        when(orgMemberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrgInvitationResponse response = acceptAction.execute(new AcceptOrgInvitationCommand(raw));

        assertThat(response.status()).isEqualTo("ACCEPTED");
        assertThat(response.token()).isNull();
        verify(invitationRepository).findByTokenHash(InvitationCodeHasher.hash(raw));
    }
}
