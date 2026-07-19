package com.company.scopery.modules.iam.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void iamEventSeeder_firstRun_createsDefinitions() {
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(any())).thenReturn(List.of());

        new IamEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues())
                .allMatch(d -> IamEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
        assertThat(captor.getAllValues())
                .extracting(d -> d.code().value())
                .contains("IAM_USER_LOGGED_IN", "IAM_PASSWORD_RESET_REQUESTED", "IAM_AUTHORIZATION_DENIED");
    }

    @Test
    void iamEventSeeder_secondRun_noDuplicates() {
        EventDefinition existing = EventDefinition.create(
                EventDefinitionCode.of("IAM_USER_CREATED"), "x",
                com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode.of("SCOPERY_IAM"),
                com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey.of("IAM_USER_CREATED"),
                "d", null, null);
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.of(existing));
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(any())).thenReturn(List.of());

        new IamEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        verify(eventDefinitionRepository, never()).save(any());
        verify(eventDefinitionRepository, never()).deleteVariablesByEventDefinitionId(any());
    }
}
