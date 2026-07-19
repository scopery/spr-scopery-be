package com.company.scopery.modules.servicesupport.sla.application.service;

import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreach;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreachRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClock;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClockRepository;
import com.company.scopery.modules.servicesupport.sla.domain.service.SlaClockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaBreachEvaluationServiceTest {
    @Mock SlaClockRepository slaClocks;
    @Mock SlaBreachRepository slaBreaches;
    SlaBreachEvaluationService service;

    @BeforeEach
    void setUp() {
        service = new SlaBreachEvaluationService(slaClocks, slaBreaches);
    }

    @Test
    void evaluateOpenClocks_recordsBreachWhenDuePassed() {
        UUID workspaceId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        UUID policyId = UUID.randomUUID();
        Instant started = Instant.now().minus(2, ChronoUnit.HOURS);
        var clock = new SlaClock(UUID.randomUUID(), workspaceId, caseId, policyId, "RESOLVE",
                started, started.plus(30, ChronoUnit.MINUTES), null, null, "RUNNING", 0, started);
        when(slaClocks.findByStatusIn(any())).thenReturn(List.of(clock));
        when(slaClocks.save(any())).thenAnswer(inv -> inv.getArgument(0));

        int recorded = service.evaluateOpenClocks();

        assertThat(recorded).isEqualTo(1);
        verify(slaBreaches).save(any(SlaBreach.class));
    }
}
