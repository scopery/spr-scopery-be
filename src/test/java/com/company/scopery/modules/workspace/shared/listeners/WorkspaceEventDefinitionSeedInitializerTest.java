package com.company.scopery.modules.workspace.shared.listeners;

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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void seedsWorkspaceEventsIdempotently_withScoperyWorkspaceSource() {
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new WorkspaceEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeastOnce()).save(captor.capture());
        assertThat(captor.getAllValues())
                .allMatch(d -> WorkspaceEventDefinitionSeedInitializer.SOURCE_SYSTEM.equals(d.sourceSystem().value()));
        assertThat(captor.getAllValues())
                .extracting(d -> d.code().value())
                .contains("ORGANIZATION_CREATED", "ORG_INVITATION_CREATED", "WORKSPACE_ARCHIVED",
                        "WORKSPACE_JOIN_REQUEST_REJECTED", "ORG_TEAM_ASSIGNED_TO_WORKSPACE");
    }

    @Test
    void doesNotRecreateExistingDefinitions() {
        EventDefinition existing = EventDefinition.create(
                EventDefinitionCode.of("ORGANIZATION_CREATED"), "x",
                com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode.of("SCOPERY_WORKSPACE"),
                com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey.of("ORGANIZATION_CREATED"),
                "d", null, null);
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.of(existing));

        new WorkspaceEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(readyEvent);

        verify(eventDefinitionRepository, never()).save(any());
    }
}
