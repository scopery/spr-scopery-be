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
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMembershipEnrollmentService;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.InvitationInboxCleanupService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
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
    @Mock private com.company.scopery.modules.iam.user.domain.model.IamUserRepository iamUserRepository;
    @Mock private com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher notificationPublisher;
    @Mock private com.company.scopery.modules.notification.shared.NotificationProperties notificationProperties;
    @Mock private InAppDeliveryService inAppDeliveryService;
    @Mock private WorkspaceAudienceResolver audienceResolver;
    @Mock private InvitationInboxCleanupService inboxCleanupService;
    @Mock private OrgMembershipEnrollmentService enrollmentService;

    private CreateOrgInvitationAction createAction;
    private AcceptOrgInvitationAction acceptAction;
    private UUID orgId;
    private IamUser actor;

    @BeforeEach
    void setUp() {
        createAction = new CreateOrgInvitationAction(
                invitationRepository, organizationRepository, orgMemberRepository,
                currentUserService, iamIntegrationService, activityLogger,
                iamUserRepository, notificationPublisher, notificationProperties,
                inAppDeliveryService, audienceResolver);
        acceptAction = new AcceptOrgInvitationAction(
                invitationRepository, enrollmentService, currentUserService, activityLogger,
                inboxCleanupService, inAppDeliveryService, audienceResolver);
        orgId = UUID.randomUUID();
        actor = IamUser.of(UUID.randomUUID(), Username.of("owner"), EmailAddress.of("o@example.com"),
                "Owner", null, IamUserStatus.ACTIVE, Instant.now(), Instant.now());
        when(currentUserService.resolveCurrentUser()).thenReturn(actor);
        lenient().when(notificationProperties.getFrontendBaseUrl()).thenReturn("http://localhost:3000");
        lenient().when(iamUserRepository.findByEmail(any())).thenReturn(Optional.empty());
        lenient().when(audienceResolver.explicitUser(any())).thenAnswer(inv -> {
            java.util.Set<UUID> s = new java.util.LinkedHashSet<>();
            UUID id = inv.getArgument(0, UUID.class);
            if (id != null) s.add(id);
            return s;
        });
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
        OrgInvitation pending = OrgInvitation.create(orgId, actor.email().value(),
                OrgMembershipType.MEMBER, actor.id(), raw, Instant.now().plusSeconds(3600));
        when(invitationRepository.findByTokenHash(InvitationCodeHasher.hash(raw))).thenReturn(Optional.of(pending));
        when(enrollmentService.ensureActiveMembership(
                eq(orgId), eq(actor.id()), eq(OrgMembershipType.MEMBER),
                eq(OrgMembershipSource.ORGANIZATION_INVITATION), any()))
                .thenReturn(OrgMember.create(orgId, actor.id(), OrgMembershipType.MEMBER,
                        OrgMembershipSource.ORGANIZATION_INVITATION));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrgInvitationResponse response = acceptAction.execute(new AcceptOrgInvitationCommand(raw));

        assertThat(response.status()).isEqualTo("ACCEPTED");
        assertThat(response.token()).isNull();
        verify(invitationRepository).findByTokenHash(InvitationCodeHasher.hash(raw));
        verify(inboxCleanupService).dismissOrgInvitation(any(), eq(actor.id()));
    }
}
