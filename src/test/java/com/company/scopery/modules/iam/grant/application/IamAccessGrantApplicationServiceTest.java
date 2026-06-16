package com.company.scopery.modules.iam.grant.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.command.CreateIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightCode;
import com.company.scopery.modules.iam.right.domain.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.IamRightStatus;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamAccessGrantApplicationServiceTest {

    @Mock private IamAccessGrantRepository grantRepository;
    @Mock private IamAccessGrantRightRepository grantRightRepository;
    @Mock private IamAuthResourceRepository resourceRepository;
    @Mock private IamRightRepository rightRepository;
    @Mock private IamActivityLogger activityLogger;

    private IamAccessGrantApplicationService service;

    private UUID subjectId;
    private UUID grantedBy;
    private IamAuthResource activeResource;
    private IamAuthResource inactiveResource;
    private IamAccessGrant activeGrant;
    private IamAccessGrant revokedGrant;
    private IamRight activeRight;
    private IamRight inactiveRight;

    @BeforeEach
    void setUp() {
        service = new IamAccessGrantApplicationService(
                grantRepository, grantRightRepository, resourceRepository, rightRepository, activityLogger);

        Instant now = Instant.now();
        subjectId = UUID.randomUUID();
        grantedBy = UUID.randomUUID();

        activeResource = new IamAuthResource(UUID.randomUUID(), IamResourceCode.of("WORKSPACE_A"),
                IamResourceType.WORKSPACE, "Workspace A", null, null, null, null, null, null,
                IamResourceStatus.ACTIVE, now, now);
        inactiveResource = new IamAuthResource(UUID.randomUUID(), IamResourceCode.of("WORKSPACE_B"),
                IamResourceType.WORKSPACE, "Workspace B", null, null, null, null, null, null,
                IamResourceStatus.INACTIVE, now, now);

        activeGrant = new IamAccessGrant(UUID.randomUUID(), IamSubjectType.USER, subjectId,
                activeResource.id(), null, IamGrantEffect.ALLOW, null, null, null,
                IamAccessGrantStatus.ACTIVE, grantedBy, now, now, now);
        revokedGrant = new IamAccessGrant(UUID.randomUUID(), IamSubjectType.USER, subjectId,
                activeResource.id(), null, IamGrantEffect.ALLOW, null, null, null,
                IamAccessGrantStatus.REVOKED, grantedBy, now, now, now);

        activeRight = new IamRight(UUID.randomUUID(), IamRightCode.of("VIEW_WORKSPACE"),
                "View Workspace", null, "WORKSPACE", IamRightStatus.ACTIVE, now, now);
        inactiveRight = new IamRight(UUID.randomUUID(), IamRightCode.of("ARCHIVE_WORKSPACE"),
                "Archive Workspace", null, "WORKSPACE", IamRightStatus.INACTIVE, now, now);
    }

    @Test
    void createGrant_validRequest_succeeds() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(grantRepository.existsBySubjectIdAndResourceId(subjectId, activeResource.id())).thenReturn(false);
        when(grantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAccessGrantResponse response = service.createGrant(
                new CreateIamAccessGrantCommand("USER", subjectId, activeResource.id(), null, null, null, null, null, grantedBy));

        assertThat(response.subjectType()).isEqualTo("USER");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(grantRepository).save(any());
    }

    @Test
    void createGrant_inactiveResource_throws422() {
        when(resourceRepository.findById(inactiveResource.id())).thenReturn(Optional.of(inactiveResource));

        assertThatThrownBy(() -> service.createGrant(
                new CreateIamAccessGrantCommand("USER", subjectId, inactiveResource.id(), null, null, null, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_AUTH_RESOURCE_INACTIVE_CANNOT_BE_USED.code()));
    }

    @Test
    void createGrant_alreadyExists_throws409() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(grantRepository.existsBySubjectIdAndResourceId(subjectId, activeResource.id())).thenReturn(true);

        assertThatThrownBy(() -> service.createGrant(
                new CreateIamAccessGrantCommand("USER", subjectId, activeResource.id(), null, null, null, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_ALREADY_EXISTS.code()));
    }

    @Test
    void createGrant_invalidSubjectType_throws400() {
        assertThatThrownBy(() -> service.createGrant(
                new CreateIamAccessGrantCommand("INVALID", subjectId, activeResource.id(), null, null, null, null, null, null)))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertThat(e.getMessage())
                        .contains(IamErrorCatalog.INVALID_IAM_SUBJECT_TYPE.code()));
    }

    @Test
    void revokeGrant_activeGrant_succeeds() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(grantRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAccessGrantResponse response = service.revokeGrant(activeGrant.id());

        assertThat(response.status()).isEqualTo("REVOKED");
    }

    @Test
    void addRight_revokedGrant_throws422() {
        when(grantRepository.findById(revokedGrant.id())).thenReturn(Optional.of(revokedGrant));

        assertThatThrownBy(() -> service.addRight(
                new AddIamGrantRightCommand(revokedGrant.id(), activeRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_REVOKED_CANNOT_BE_MODIFIED.code()));
    }

    @Test
    void addRight_inactiveRight_throws422() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(rightRepository.findById(inactiveRight.id())).thenReturn(Optional.of(inactiveRight));

        assertThatThrownBy(() -> service.addRight(
                new AddIamGrantRightCommand(activeGrant.id(), inactiveRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_RIGHT_INACTIVE_CANNOT_BE_USED.code()));
    }

    @Test
    void addRight_alreadyAttached_throws409() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(rightRepository.findById(activeRight.id())).thenReturn(Optional.of(activeRight));
        when(grantRightRepository.existsByGrantIdAndRightId(activeGrant.id(), activeRight.id())).thenReturn(true);

        assertThatThrownBy(() -> service.addRight(
                new AddIamGrantRightCommand(activeGrant.id(), activeRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_RIGHT_ALREADY_EXISTS.code()));
    }

    @Test
    void addRight_validRequest_succeeds() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(rightRepository.findById(activeRight.id())).thenReturn(Optional.of(activeRight));
        when(grantRightRepository.existsByGrantIdAndRightId(activeGrant.id(), activeRight.id())).thenReturn(false);
        when(grantRightRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAccessGrantRightResponse response = service.addRight(
                new AddIamGrantRightCommand(activeGrant.id(), activeRight.id()));

        assertThat(response.grantId()).isEqualTo(activeGrant.id());
        assertThat(response.rightId()).isEqualTo(activeRight.id());
    }

    @Test
    void removeRight_notAttached_throws404() {
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(grantRightRepository.existsByGrantIdAndRightId(activeGrant.id(), activeRight.id())).thenReturn(false);

        assertThatThrownBy(() -> service.removeRight(
                new RemoveIamGrantRightCommand(activeGrant.id(), activeRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_RIGHT_NOT_FOUND.code()));
    }

    @Test
    void removeRight_revokedGrant_throws422() {
        when(grantRepository.findById(revokedGrant.id())).thenReturn(Optional.of(revokedGrant));

        assertThatThrownBy(() -> service.removeRight(
                new RemoveIamGrantRightCommand(revokedGrant.id(), activeRight.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ACCESS_GRANT_REVOKED_CANNOT_BE_MODIFIED.code()));
    }

    @Test
    void getGrantRights_existingGrant_returnsList() {
        Instant now = Instant.now();
        IamAccessGrantRight grantRight = new IamAccessGrantRight(activeGrant.id(), activeRight.id(), now);
        when(grantRepository.findById(activeGrant.id())).thenReturn(Optional.of(activeGrant));
        when(grantRightRepository.findByGrantId(activeGrant.id())).thenReturn(List.of(grantRight));

        List<IamAccessGrantRightResponse> rights = service.getGrantRights(activeGrant.id());

        assertThat(rights).hasSize(1);
        assertThat(rights.get(0).rightId()).isEqualTo(activeRight.id());
    }
}
