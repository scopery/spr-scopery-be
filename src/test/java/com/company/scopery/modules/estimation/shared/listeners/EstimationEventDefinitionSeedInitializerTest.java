package com.company.scopery.modules.estimation.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstimationEventDefinitionSeedInitializerTest {

    @Mock EventDefinitionRepository eventDefinitionRepository;
    @Mock ApplicationReadyEvent event;

    @Test
    void seedsRequiredEvents() {
        when(eventDefinitionRepository.findByCode(any(EventDefinitionCode.class))).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new EstimationEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeast(10)).save(captor.capture());
        assertThat(captor.getAllValues().stream().map(d -> d.code().value()))
                .contains("ESTIMATION_RUN_CREATED", "ESTIMATION_RUN_COMPLETED",
                        "TASK_ESTIMATE_RATE_UNRESOLVED", "PROJECT_ESTIMATE_SUMMARY_CREATED");
        assertThat(EstimationEventDefinitionSeedInitializer.SOURCE_SYSTEM).isEqualTo("SCOPERY_ESTIMATION");
        assertThat(EstimationEventDefinitionSeedInitializer.OWNER_MODULE).isEqualTo("ESTIMATION");
    }

    @Test
    void secondRunDoesNotDuplicate() {
        when(eventDefinitionRepository.findByCode(any(EventDefinitionCode.class)))
                .thenAnswer(inv -> Optional.of(org.mockito.Mockito.mock(EventDefinition.class)));

        new EstimationEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);
        new EstimationEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);

        verify(eventDefinitionRepository, org.mockito.Mockito.never()).save(any());
    }
}
