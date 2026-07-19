package com.company.scopery.modules.servicesupport.sla.application.service;

import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreach;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaBreachRepository;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaClockRepository;
import com.company.scopery.modules.servicesupport.sla.domain.service.SlaClockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SlaBreachEvaluationService {
    private static final Logger log = LoggerFactory.getLogger(SlaBreachEvaluationService.class);

    private final SlaClockRepository slaClocks;
    private final SlaBreachRepository slaBreaches;

    public SlaBreachEvaluationService(SlaClockRepository slaClocks, SlaBreachRepository slaBreaches) {
        this.slaClocks = slaClocks;
        this.slaBreaches = slaBreaches;
    }

    @Transactional
    public int evaluateOpenClocks() {
        Instant now = Instant.now();
        int recorded = 0;
        for (var clock : slaClocks.findByStatusIn(List.of("RUNNING", "PAUSED"))) {
            if (!SlaClockService.isBreached(clock.dueAt(), now)) continue;
            var breachedClock = slaClocks.save(clock.markBreached());
            slaBreaches.save(SlaBreach.open(
                    breachedClock.workspaceId(), breachedClock.supportCaseId(),
                    breachedClock.id(), breachedClock.clockType()));
            recorded++;
        }
        if (recorded > 0) {
            log.info("SLA breach evaluation recorded {} new breaches", recorded);
        }
        return recorded;
    }
}
