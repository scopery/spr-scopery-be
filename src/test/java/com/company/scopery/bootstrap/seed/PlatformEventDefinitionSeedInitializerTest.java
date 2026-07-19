package com.company.scopery.bootstrap.seed;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformEventDefinitionSeedInitializerTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private ApplicationReadyEvent readyEvent;

    private PlatformEventDefinitionSeedInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new PlatformEventDefinitionSeedInitializer(eventDefinitionRepository);
    }

    @Test
    void firstRun_createsPlatformEvents() {
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        initializer.onApplicationEvent(readyEvent);

        verify(eventDefinitionRepository, times(5)).save(any(EventDefinition.class));
    }

    @Test
    void secondRun_noDuplicates() {
        EventDefinition existing = EventDefinition.create(
                EventDefinitionCode.of("PLATFORM_JOB_FAILED"),
                "Platform Job Failed",
                SourceSystemCode.of(PlatformEventDefinitionSeedInitializer.SOURCE_SYSTEM),
                EventKey.of("PLATFORM_JOB_FAILED"),
                "desc", null, null);
        when(eventDefinitionRepository.findByCode(any())).thenReturn(Optional.of(existing));

        initializer.onApplicationEvent(readyEvent);

        verify(eventDefinitionRepository, never()).save(any());
    }

    @Test
    void isKnown_delegatesToRepository() {
        when(eventDefinitionRepository.existsByCode(EventDefinitionCode.of("PLATFORM_JOB_FAILED"))).thenReturn(true);
        assertThat(initializer.isKnown("PLATFORM_JOB_FAILED")).isTrue();
    }
}
