package com.company.scopery.modules.aiplanning.shared.support;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.aiplanning.planningrun.domain.model.AiPlanningRun;
import com.company.scopery.modules.aiplanning.shared.listeners.AiPlanningEventDefinitionSeedInitializer;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AiPlanningPlatformPublisher {
    public static final String AGGREGATE_RUN = "AI_PLANNING_RUN";
    public static final String AGGREGATE_SUGGESTION = "AI_PLANNING_SUGGESTION";

    private final TransactionalOutboxService outboxService;

    public AiPlanningPlatformPublisher(TransactionalOutboxService outboxService) {
        this.outboxService = outboxService;
    }

    public void enqueueRun(AiPlanningRun run, String eventCode) {
        outboxService.enqueue(AGGREGATE_RUN, run.id(), eventCode,
                AiPlanningEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1,
                mapOf("planningRunId", run.id(), "projectId", run.projectId(),
                        "workspaceId", run.workspaceId(), "runType", run.runType().name(),
                        "status", run.status().name()));
    }

    public void enqueueSuggestion(AiPlanningSuggestion suggestion, String eventCode) {
        outboxService.enqueue(AGGREGATE_SUGGESTION, suggestion.id(), eventCode,
                AiPlanningEventDefinitionSeedInitializer.SOURCE_SYSTEM, 1,
                mapOf("suggestionId", suggestion.id(), "planningRunId", suggestion.planningRunId(),
                        "projectId", suggestion.projectId(), "status", suggestion.status().name(),
                        "suggestionType", suggestion.suggestionType().name()));
    }

    public static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
