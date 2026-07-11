package com.company.scopery.modules.iam.resource.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.modules.iam.resource.application.command.CreateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.DeactivateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.command.UpdateIamAuthResourceCommand;
import com.company.scopery.modules.iam.resource.application.response.IamAuthResourceResponse;
import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceQueryService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamAuthResourceActionTest {

    @Mock private IamAuthResourceRepository resourceRepository;
    @Mock private IamActivityLogger activityLogger;

    private CreateIamAuthResourceAction createAction;
    private UpdateIamAuthResourceAction updateAction;
    private DeactivateIamAuthResourceAction deactivateAction;
    private IamAuthResourceQueryService queryService;

    private IamAuthResource activeResource;

    @BeforeEach
    void setUp() {
        createAction = new CreateIamAuthResourceAction(resourceRepository, activityLogger);
        updateAction = new UpdateIamAuthResourceAction(resourceRepository, activityLogger);
        deactivateAction = new DeactivateIamAuthResourceAction(resourceRepository, activityLogger);
        queryService = new IamAuthResourceQueryService(resourceRepository);

        activeResource = IamAuthResource.create(
                IamResourceCode.of("GLOBAL_SYSTEM"), IamResourceType.GLOBAL, "Global System", null);
    }

    @Test
    void createResource_newCode_succeeds() {
        when(resourceRepository.existsByCodeAndResourceType(any(), any())).thenReturn(false);
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAuthResourceResponse response = createAction.execute(
                new CreateIamAuthResourceCommand("GLOBAL_SYSTEM", "GLOBAL", "Global System", null));

        assertThat(response.code()).isEqualTo("GLOBAL_SYSTEM");
        assertThat(response.resourceType()).isEqualTo("GLOBAL");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void createResource_nonGlobalResource_throws422() {
        assertThatThrownBy(() -> createAction.execute(
                new CreateIamAuthResourceCommand("WORKSPACE_A", "WORKSPACE", "Workspace A", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_AUTH_RESOURCE_MANUAL_CREATE_REQUIRES_GLOBAL.code()));
    }

    @Test
    void createResource_invalidResourceType_throws400() {
        assertThatThrownBy(() -> createAction.execute(
                new CreateIamAuthResourceCommand("CODE", "INVALID_TYPE", "Name", null)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getResource_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(resourceRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryService.getResource(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_AUTH_RESOURCE_NOT_FOUND.code()));
    }

    @Test
    void deactivateResource_activeResource_succeeds() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAuthResourceResponse response = deactivateAction.execute(
                new DeactivateIamAuthResourceCommand(activeResource.id()));

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void updateResource_existingResource_succeeds() {
        when(resourceRepository.findById(activeResource.id())).thenReturn(Optional.of(activeResource));
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamAuthResourceResponse response = updateAction.execute(
                new UpdateIamAuthResourceCommand(activeResource.id(), "Updated Name", "New desc"));

        assertThat(response.name()).isEqualTo("Updated Name");
    }
}
