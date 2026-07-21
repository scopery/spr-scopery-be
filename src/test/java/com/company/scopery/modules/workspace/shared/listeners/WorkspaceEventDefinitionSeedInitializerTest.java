package com.company.scopery.modules.workspace.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private ApplicationReadyEvent readyEvent;

    @Test
    void seedsWorkspaceEventsViaUpsert() {
        UUID stubId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(UUID.class), any(Object[].class)))
                .thenReturn(stubId);

        new WorkspaceEventDefinitionSeedInitializer(eventDefinitionRepository, jdbcTemplate)
                .onApplicationEvent(readyEvent);

        verify(jdbcTemplate, atLeast(30)).queryForObject(anyString(), eq(UUID.class), any(Object[].class));
    }

    @Test
    void seedingIsIdempotent_noExceptionOnConflict() {
        UUID stubId = UUID.randomUUID();
        when(jdbcTemplate.queryForObject(anyString(), eq(UUID.class), any(Object[].class)))
                .thenReturn(stubId);

        WorkspaceEventDefinitionSeedInitializer init =
                new WorkspaceEventDefinitionSeedInitializer(eventDefinitionRepository, jdbcTemplate);

        init.onApplicationEvent(readyEvent);
        init.onApplicationEvent(readyEvent);

        verify(jdbcTemplate, atLeast(60)).queryForObject(anyString(), eq(UUID.class), any(Object[].class));
    }
}
