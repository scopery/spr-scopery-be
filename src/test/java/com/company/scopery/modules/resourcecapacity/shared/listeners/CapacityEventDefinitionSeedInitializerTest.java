package com.company.scopery.modules.resourcecapacity.shared.listeners;

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
class CapacityEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void capacityEventSeeder_firstRun_createsDefinitions() {
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new CapacityEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues())
                .allMatch(d -> CapacityEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
        assertThat(captor.getAllValues())
                .extracting(d -> d.code().value())
                .contains("CAPACITY_CALENDAR_CREATED", "CAPACITY_DAY_RULES_UPDATED", "CAPACITY_EXCEPTION_CREATED",
                        "CAPACITY_PROFILE_CREATED", "PROJECT_ALLOCATION_CREATED",
                        "RESOURCE_PROFILE_CREATED", "EFFORT_FORECAST_REBUILT", "RESOURCE_COST_INPUT_REBUILT",
                        "WORKLOAD_SNAPSHOT_CREATED", "RESOURCE_OVERLOAD_DETECTED");
        assertThat(captor.getAllValues()).hasSize(33);
    }

    @Test
    void capacityEventSeeder_existingDefinitions_doesNotRecreate() {
        EventDefinition existing = EventDefinition.create(
                EventDefinitionCode.of("CAPACITY_CALENDAR_CREATED"), "Working Calendar Created",
                SourceSystemCode.of("SCOPERY_CAPACITY"), EventKey.of("CAPACITY_CALENDAR_CREATED"),
                "Working calendar created", null, null);
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.of(existing));

        new CapacityEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        verify(eventDefinitionRepository, never()).save(any());
    }
}
