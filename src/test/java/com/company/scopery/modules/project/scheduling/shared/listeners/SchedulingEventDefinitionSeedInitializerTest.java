package com.company.scopery.modules.project.scheduling.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.*;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingEventDefinitionSeedInitializerTest {
    @Mock EventDefinitionRepository repository; @Mock ApplicationReadyEvent event;
    @Test void firstRunCreatesDefinitions(){
        when(repository.findByCode(any())).thenReturn(Optional.empty());when(repository.save(any())).thenAnswer(i->i.getArgument(0));
        new SchedulingEventDefinitionSeedInitializer(repository).onApplicationEvent(event);
        ArgumentCaptor<EventDefinition> captor=ArgumentCaptor.forClass(EventDefinition.class);verify(repository,times(13)).save(captor.capture());
        assertThat(captor.getAllValues()).allMatch(d->SchedulingEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
        assertThat(captor.getAllValues()).extracting(d->d.code().value()).contains("SCHEDULE_RUN_CREATED","TASK_SCHEDULE_AT_RISK","SCHEDULING_ISSUE_CREATED");
    }
    @Test void existingDefinitionsAreIdempotent(){
        EventDefinition existing=EventDefinition.create(EventDefinitionCode.of("SCHEDULE_RUN_CREATED"),"Existing",
                SourceSystemCode.of(SchedulingEventDefinitionSeedInitializer.SOURCE_SYSTEM),EventKey.of("SCHEDULE_RUN_CREATED"),"Existing",null,null);
        when(repository.findByCode(any())).thenReturn(Optional.of(existing));
        new SchedulingEventDefinitionSeedInitializer(repository).onApplicationEvent(event);
        verify(repository,never()).save(any());
    }
}
