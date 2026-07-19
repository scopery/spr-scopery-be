package com.company.scopery.modules.servicesupport.sla.application.jobs;

import com.company.scopery.modules.servicesupport.sla.application.service.SlaBreachEvaluationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SlaBreachEvaluationJob {
    private final SlaBreachEvaluationService evaluationService;

    public SlaBreachEvaluationJob(SlaBreachEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Scheduled(cron = "${scopery.support.sla.breach-eval-cron:0 */5 * * * *}")
    public void evaluateBreaches() {
        evaluationService.evaluateOpenClocks();
    }
}
