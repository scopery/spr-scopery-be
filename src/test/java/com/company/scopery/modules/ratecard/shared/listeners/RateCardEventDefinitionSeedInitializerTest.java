package com.company.scopery.modules.ratecard.shared.listeners;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateCardEventDefinitionSeedInitializerTest {

    @Mock EventDefinitionRepository eventDefinitionRepository;
    @Mock ApplicationReadyEvent event;

    @Test
    void seedsRequiredEvents() {
        when(eventDefinitionRepository.findByCode(any(EventDefinitionCode.class))).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new RateCardEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeast(20)).save(captor.capture());
        assertThat(captor.getAllValues().stream().map(d -> d.code().value()))
                .contains("COST_ROLE_CREATED", "RATE_CARD_VERSION_PUBLISHED", "RATE_RESOLVED", "INFLATION_POLICY_CREATED");
        assertThat(RateCardEventDefinitionSeedInitializer.SOURCE_SYSTEM).isEqualTo("SCOPERY_RATE_CARD");
        assertThat(RateCardEventDefinitionSeedInitializer.OWNER_MODULE).isEqualTo("RATE_CARD");
    }
}
