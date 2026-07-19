package com.company.scopery.modules.project.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
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
class ProjectEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void projectEventSeeder_createsKnownProjectEvents() {
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(any())).thenReturn(List.of());

        new ProjectEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(d -> d.code().value())
                .contains(
                        "PROJECT_CREATED",
                        "TASK_ASSIGNED",
                        "WBS_NODE_CREATED",
                        "PROJECT_PHASE_UPDATED",
                        "PHASE_DEFINITION_CREATED",
                        "PROJECT_TEMPLATE_CREATED",
                        "PROJECT_TEMPLATE_VERSION_PUBLISHED",
                        "PROJECT_TEMPLATE_APPLIED");
        assertThat(captor.getAllValues())
                .allMatch(d -> ProjectEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
    }
}
