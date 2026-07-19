package com.company.scopery.modules.aiplanning.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiPlanningEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void aiPlanningEventSeeder_firstRun_createsDefinitions() {
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new AiPlanningEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues())
                .allMatch(d -> AiPlanningEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
        assertThat(captor.getAllValues())
                .extracting(d -> d.code().value())
                .contains(
                        "AI_PLANNING_RUN_CREATED",
                        "AI_PLANNING_SUGGESTION_GENERATED",
                        "AI_PLANNING_SUGGESTION_APPLIED",
                        "AI_PLANNING_APPLY_FAILED");
        assertThat(captor.getAllValues()).hasSize(AiPlanningEventDefinitionSeedInitializer.EVENTS.size());
    }

    @Test
    void aiPlanningEventSeeder_secondRun_noDuplicates() {
        EventDefinition existing = EventDefinition.create(
                EventDefinitionCode.of("AI_PLANNING_RUN_CREATED"), "AI Planning Run Created",
                SourceSystemCode.of(AiPlanningEventDefinitionSeedInitializer.SOURCE_SYSTEM),
                EventKey.of("AI_PLANNING_RUN_CREATED"),
                "AI planning run created", null, null);
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.of(existing));

        new AiPlanningEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        verify(eventDefinitionRepository, never()).save(any());
    }
}
