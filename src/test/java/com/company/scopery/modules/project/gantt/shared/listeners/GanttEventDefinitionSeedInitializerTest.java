package com.company.scopery.modules.project.gantt.shared.listeners;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GanttEventDefinitionSeedInitializerTest {

    @Mock EventDefinitionRepository repository;
    @Mock ApplicationReadyEvent event;

    @Test
    void firstRunCreatesDefinitions() {
        when(repository.findByCode(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        new GanttEventDefinitionSeedInitializer(repository).onApplicationEvent(event);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(repository, times(13)).save(captor.capture());
        assertThat(captor.getAllValues()).allMatch(d ->
                GanttEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
        assertThat(captor.getAllValues()).extracting(d -> d.code().value())
                .contains("GANTT_RECALCULATED", "GANTT_TASK_MOVED", "PROJECT_MILESTONE_CREATED");
    }

    @Test
    void existingDefinitionsAreIdempotent() {
        EventDefinition existing = EventDefinition.create(
                EventDefinitionCode.of("GANTT_RECALCULATED"),
                "Existing",
                SourceSystemCode.of(GanttEventDefinitionSeedInitializer.SOURCE_SYSTEM),
                EventKey.of("GANTT_RECALCULATED"),
                "Existing", null, null);
        when(repository.findByCode(any())).thenReturn(Optional.of(existing));

        new GanttEventDefinitionSeedInitializer(repository).onApplicationEvent(event);

        verify(repository, never()).save(any());
    }
}
