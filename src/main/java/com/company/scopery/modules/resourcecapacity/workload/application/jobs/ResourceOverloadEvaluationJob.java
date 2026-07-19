package com.company.scopery.modules.resourcecapacity.workload.application.jobs;

import com.company.scopery.modules.resourcecapacity.workload.application.service.ResourceOverloadEvaluationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResourceOverloadEvaluationJob {

    private final ResourceOverloadEvaluationService evaluationService;

    public ResourceOverloadEvaluationJob(ResourceOverloadEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Scheduled(cron = "${scopery.resource-capacity.overload-eval-cron:0 */10 * * * *}")
    public void evaluateOverloads() {
        evaluationService.evaluateAll();
    }
}
