package com.company.scopery.modules.iam.resource.application.service;

import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceVisibility;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamAuthResourceLifecycleServiceTest {

    @Mock private IamAuthResourceRepository resourceRepository;
    @Mock private IamActivityLogger activityLogger;

    private IamAuthResourceLifecycleService service;
    private UUID refId;
    private IamAuthResource resource;

    @BeforeEach
    void setUp() {
        service = new IamAuthResourceLifecycleService(resourceRepository, activityLogger);
        refId = UUID.randomUUID();
        resource = IamAuthResource.createWithOwnership(
                IamResourceCode.of("WS_A"), IamResourceType.WORKSPACE, "Old", null,
                refId, null, null, refId, IamResourceVisibility.PRIVATE, null);
    }

    @Test
    void syncDisplayName_updatesWhenFound() {
        when(resourceRepository.findByRefIdAndResourceType(refId, IamResourceType.WORKSPACE))
                .thenReturn(Optional.of(resource));
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.syncDisplayName(refId, IamResourceType.WORKSPACE, "New Name", "desc");

        ArgumentCaptor<IamAuthResource> captor = ArgumentCaptor.forClass(IamAuthResource.class);
        verify(resourceRepository).save(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("New Name");
        assertThat(captor.getValue().description()).isEqualTo("desc");
    }

    @Test
    void deactivateByRef_idempotentWhenAlreadyInactive() {
        when(resourceRepository.findByRefIdAndResourceType(refId, IamResourceType.WORKSPACE))
                .thenReturn(Optional.of(resource.deactivate()));

        service.deactivateByRef(refId, IamResourceType.WORKSPACE);

        verify(resourceRepository, never()).save(any());
    }

    @Test
    void deactivateByRef_deactivatesActiveResource() {
        when(resourceRepository.findByRefIdAndResourceType(refId, IamResourceType.WORKSPACE))
                .thenReturn(Optional.of(resource));
        when(resourceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.deactivateByRef(refId, IamResourceType.WORKSPACE);

        ArgumentCaptor<IamAuthResource> captor = ArgumentCaptor.forClass(IamAuthResource.class);
        verify(resourceRepository).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(IamResourceStatus.INACTIVE);
    }
}
