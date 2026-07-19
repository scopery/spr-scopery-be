package com.company.scopery.modules.ratecard.shared.listeners;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRole;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CostRoleSeedInitializerTest {

    @Mock CostRoleRepository costRoleRepository;
    @Mock ApplicationReadyEvent event;

    @Test
    void seedsBuiltInRolesIdempotently() {
        when(costRoleRepository.existsByScopeAndCode(eq(CostRoleScope.SYSTEM), any(), any(), any()))
                .thenReturn(false);
        when(costRoleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new CostRoleSeedInitializer(costRoleRepository).onApplicationEvent(event);

        ArgumentCaptor<CostRole> captor = ArgumentCaptor.forClass(CostRole.class);
        verify(costRoleRepository, times(CostRoleSeedInitializer.seeds().size())).save(captor.capture());
        assertThat(captor.getAllValues()).allMatch(CostRole::builtIn);
        assertThat(captor.getAllValues()).allMatch(r -> r.scope() == CostRoleScope.SYSTEM);
        assertThat(captor.getAllValues().stream().map(CostRole::code))
                .contains("PROJECT_MANAGER", "BACKEND_DEVELOPER", "AI_ENGINEER");
    }

    @Test
    void skipsExistingRoles() {
        when(costRoleRepository.existsByScopeAndCode(eq(CostRoleScope.SYSTEM), any(), any(), any()))
                .thenReturn(true);
        new CostRoleSeedInitializer(costRoleRepository).onApplicationEvent(event);
        verify(costRoleRepository, never()).save(any());
    }
}
