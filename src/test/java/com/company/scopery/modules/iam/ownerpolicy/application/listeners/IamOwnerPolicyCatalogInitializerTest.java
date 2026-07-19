package com.company.scopery.modules.iam.ownerpolicy.application.listeners;

import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicyRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamOwnerPolicyCatalogInitializerTest {

    @Mock private IamOwnerPolicyRepository ownerPolicyRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void onApplicationEvent_seedsMissingPoliciesOnly() {
        when(ownerPolicyRepository.findActiveByResourceType(IamResourceType.ORGANIZATION))
                .thenReturn(Optional.of(mock(IamOwnerPolicy.class)));
        when(ownerPolicyRepository.findActiveByResourceType(IamResourceType.WORKSPACE))
                .thenReturn(Optional.empty());
        when(ownerPolicyRepository.findActiveByResourceType(IamResourceType.TEAM))
                .thenReturn(Optional.empty());
        when(ownerPolicyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new IamOwnerPolicyCatalogInitializer(ownerPolicyRepository).onApplicationEvent(readyEvent);

        ArgumentCaptor<IamOwnerPolicy> captor = ArgumentCaptor.forClass(IamOwnerPolicy.class);
        verify(ownerPolicyRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(IamOwnerPolicy::resourceType)
                .containsExactlyInAnyOrder(IamResourceType.WORKSPACE, IamResourceType.TEAM);
    }

    @Test
    void onApplicationEvent_idempotentWhenAllPresent() {
        when(ownerPolicyRepository.findActiveByResourceType(any()))
                .thenReturn(Optional.of(mock(IamOwnerPolicy.class)));

        new IamOwnerPolicyCatalogInitializer(ownerPolicyRepository).onApplicationEvent(readyEvent);

        verify(ownerPolicyRepository, never()).save(any());
    }
}
