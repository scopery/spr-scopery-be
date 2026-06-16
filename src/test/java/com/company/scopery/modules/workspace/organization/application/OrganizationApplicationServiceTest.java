package com.company.scopery.modules.workspace.organization.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.workspace.organization.application.command.CreateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.command.UpdateOrganizationCommand;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.Organization;
import com.company.scopery.modules.workspace.organization.domain.OrganizationCode;
import com.company.scopery.modules.workspace.organization.domain.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationApplicationServiceTest {

    @Mock private OrganizationRepository organizationRepository;
    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;

    private OrganizationApplicationService service;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        service = new OrganizationApplicationService(
                organizationRepository, activityLogger, currentUserService, iamIntegrationService);
        Instant now = Instant.now();
        currentUser = new IamUser(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createOrganization_success_returnsActiveOrganizationResponse() {
        CreateOrganizationCommand command = new CreateOrganizationCommand("Acme Corp", "ACME", "A test org");

        when(organizationRepository.existsByCode(any())).thenReturn(false);
        when(organizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(iamIntegrationService.bootstrapOrganizationAccess(any(), any(), any())).thenReturn(UUID.randomUUID());

        OrganizationResponse response = service.createOrganization(command);

        assertThat(response.name()).isEqualTo("Acme Corp");
        assertThat(response.code()).isEqualTo("ACME");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(organizationRepository).save(any(Organization.class));
        verify(activityLogger).logSuccess(eq("ORGANIZATION"), any(UUID.class),
                eq("CREATE_ORGANIZATION"), any(String.class));
    }

    @Test
    void createOrganization_duplicateCode_throwsConflict() {
        CreateOrganizationCommand command = new CreateOrganizationCommand("Acme Corp", "ACME", null);

        when(organizationRepository.existsByCode(any())).thenReturn(true);

        assertThatThrownBy(() -> service.createOrganization(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.ORGANIZATION_CODE_ALREADY_EXISTS.code());
                });

        verify(organizationRepository, never()).save(any());
    }

    @Test
    void updateOrganization_success_returnsUpdatedResponse() {
        UUID orgId = UUID.randomUUID();
        Organization existing = existingOrganization(orgId, OrganizationStatus.ACTIVE);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(existing));
        when(organizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateOrganizationCommand command = new UpdateOrganizationCommand(orgId, "New Name", "New desc");
        OrganizationResponse response = service.updateOrganization(command);

        assertThat(response.name()).isEqualTo("New Name");
        assertThat(response.description()).isEqualTo("New desc");
        verify(activityLogger).logSuccess(eq("ORGANIZATION"), any(UUID.class),
                eq("UPDATE_ORGANIZATION"), any(String.class));
    }

    @Test
    void activateOrganization_success_returnsActiveStatus() {
        UUID orgId = UUID.randomUUID();
        Organization existing = existingOrganization(orgId, OrganizationStatus.INACTIVE);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(existing));
        when(organizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrganizationResponse response = service.activateOrganization(orgId);

        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(activityLogger).logSuccess(eq("ORGANIZATION"), any(UUID.class),
                eq("ACTIVATE_ORGANIZATION"), any(String.class));
    }

    @Test
    void archiveOrganization_success_returnsArchivedStatus() {
        UUID orgId = UUID.randomUUID();
        Organization existing = existingOrganization(orgId, OrganizationStatus.ACTIVE);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(existing));
        when(organizationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrganizationResponse response = service.archiveOrganization(orgId);

        assertThat(response.status()).isEqualTo("ARCHIVED");
        verify(activityLogger).logSuccess(eq("ORGANIZATION"), any(UUID.class),
                eq("ARCHIVE_ORGANIZATION"), any(String.class));
    }

    @Test
    void updateOrganization_archivedOrganization_throwsUnprocessable() {
        UUID orgId = UUID.randomUUID();
        Organization archived = existingOrganization(orgId, OrganizationStatus.ARCHIVED);
        UpdateOrganizationCommand command = new UpdateOrganizationCommand(orgId, "New Name", null);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> service.updateOrganization(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.ORGANIZATION_ARCHIVED_CANNOT_BE_UPDATED.code());
                });

        verify(organizationRepository, never()).save(any());
    }

    @Test
    void getOrganization_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrganization(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    private Organization existingOrganization(UUID id, OrganizationStatus status) {
        Instant now = Instant.now();
        return new Organization(id, OrganizationCode.of("ACME"), "Acme Corp", "A corp",
                UUID.randomUUID(), status, now, now);
    }
}
