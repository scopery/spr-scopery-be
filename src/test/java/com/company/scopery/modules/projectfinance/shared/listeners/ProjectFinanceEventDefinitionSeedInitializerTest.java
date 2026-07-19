package com.company.scopery.modules.projectfinance.shared.listeners;

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
class ProjectFinanceEventDefinitionSeedInitializerTest {

    @Mock EventDefinitionRepository eventDefinitionRepository;
    @Mock ApplicationReadyEvent event;

    @Test
    void seedsRequiredEvents() {
        when(eventDefinitionRepository.findByCode(any(EventDefinitionCode.class))).thenReturn(Optional.empty());
        when(eventDefinitionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new ProjectFinanceEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);

        ArgumentCaptor<EventDefinition> captor = ArgumentCaptor.forClass(EventDefinition.class);
        verify(eventDefinitionRepository, atLeast(10)).save(captor.capture());
        assertThat(captor.getAllValues().stream().map(d -> d.code().value()))
                .contains("PROJECT_FINANCE_SCENARIO_CREATED",
                        "PROJECT_FINANCE_SCENARIO_APPROVED",
                        "PROJECT_FINANCE_SUMMARY_CALCULATED",
                        "PROJECT_CUSTOM_COST_CREATED");
        assertThat(ProjectFinanceEventDefinitionSeedInitializer.SOURCE_SYSTEM)
                .isEqualTo("SCOPERY_PROJECT_FINANCE");
        assertThat(ProjectFinanceEventDefinitionSeedInitializer.OWNER_MODULE)
                .isEqualTo("PROJECT_FINANCE");
    }

    @Test
    void secondRunDoesNotDuplicate() {
        when(eventDefinitionRepository.findByCode(any(EventDefinitionCode.class)))
                .thenAnswer(inv -> Optional.of(org.mockito.Mockito.mock(EventDefinition.class)));

        new ProjectFinanceEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);
        new ProjectFinanceEventDefinitionSeedInitializer(eventDefinitionRepository).onApplicationEvent(event);

        verify(eventDefinitionRepository, org.mockito.Mockito.never()).save(any());
    }
}
