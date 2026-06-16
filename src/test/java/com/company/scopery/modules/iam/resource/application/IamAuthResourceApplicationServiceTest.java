package com.company.scopery.modules.iam.resource.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.resource.application.command.CreateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.UpdateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamAuthResourceApplicationServiceTest {

    @Mock
    private IamAuthResourceRepository resourceRepository;

    @Mock
    private IamActivityLogger activityLogger;

    private IamAuthResourceApplicationService service;
    private IamAuthResource activeResource;

    @BeforeEach
    void setUp() {
        service = new IamAuthResourceApplicationService(resourceRepository, activityLogger);
        Instant now = Instant.now();
        activeResource = new IamAuthResource(UUID.randomUUID(),
                IamResourceCode.of("GLOBAL_WORKSPACE"), IamResourceType.WORKSPACE,
                "Global Workspace", null, null, null, null, null, null,
                IamResourceStatus.ACTIVE, now, now);
    }

    @Test
    void createResource_newCode_succeeds() {
        when(resourceRepository.existsByCodeAndResourceType(any(), any())).thenReturn(false);
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAuthResourceResponse response = service.createResource(
                new CreateIamAuthResourceCommand("GLOBAL_WORKSPACE", "WORKSPACE",
                        "Global Workspace", null));

        assertThat(response.code()).isEqualTo("GLOBAL_WORKSPACE");
        assertThat(response.resourceType()).isEqualTo("WORKSPACE");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void createResource_duplicateCodeAndType_throws409() {
        when(resourceRepository.existsByCodeAndResourceType(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createResource(
                new CreateIamAuthResourceCommand("GLOBAL_WORKSPACE", "WORKSPACE", "Name", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_AUTH_RESOURCE_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void createResource_invalidResourceType_throws400() {
        assertThatThrownBy(() -> service.createResource(
                new CreateIamAuthResourceCommand("CODE", "INVALID_TYPE", "Name", null)))
                .isInstanceOf(ValidationException.class)
                .satisfies(e -> assertThat(e.getMessage())
                        .contains(IamErrorCatalog.INVALID_IAM_RESOURCE_TYPE.code()));
    }

    @Test
    void getResource_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(resourceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getResource(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_AUTH_RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void deactivateResource_activeResource_succeeds() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAuthResourceResponse response = service.deactivateResource(activeResource.id());

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void updateResource_existingResource_succeeds() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAuthResourceResponse response = service.updateResource(
                new UpdateIamAuthResourceCommand(activeResource.id(), "Updated Name", "New desc"));

        assertThat(response.name()).isEqualTo("Updated Name");
    }
}
