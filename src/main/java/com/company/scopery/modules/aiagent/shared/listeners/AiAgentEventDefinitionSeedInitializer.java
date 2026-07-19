package com.company.scopery.modules.aiagent.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds Phase 07 AI Agent event contracts (sourceSystem = SCOPERY_AI_AGENT).
 */
@Component
@Order(12)
public class AiAgentEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiAgentEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_AI_AGENT";
    public static final String OWNER_MODULE = "AI_AGENT";

    private final EventDefinitionRepository eventDefinitionRepository;

    public AiAgentEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (SeedEvent seed : EVENTS) {
            EventDefinitionSeedSupport.findOrCreate(
                    eventDefinitionRepository,
                    SOURCE_SYSTEM,
                    seed.code(),
                    seed.name(),
                    seed.description(),
                    EventDataClassification.INTERNAL,
                    OWNER_MODULE);
        }
        log.info("[AiAgentEventDefinitionSeed] AI Agent event seeding complete ({} events)", EVENTS.size());
    }

    private record SeedEvent(String code, String name, String description) {}

    private static final List<SeedEvent> EVENTS = List.of(
            new SeedEvent("AI_PROVIDER_CREATED", "AI Provider Created", "Provider catalog entry created"),
            new SeedEvent("AI_PROVIDER_UPDATED", "AI Provider Updated", "Provider catalog entry updated"),
            new SeedEvent("AI_PROVIDER_DEACTIVATED", "AI Provider Deactivated", "Provider deactivated"),
            new SeedEvent("AI_MODEL_CREATED", "AI Model Created", "AI model created"),
            new SeedEvent("AI_MODEL_UPDATED", "AI Model Updated", "AI model updated"),
            new SeedEvent("AI_MODEL_DEACTIVATED", "AI Model Deactivated", "AI model deactivated"),
            new SeedEvent("AI_DEPLOYMENT_CREATED", "AI Deployment Created", "Model deployment created"),
            new SeedEvent("AI_DEPLOYMENT_ACTIVATED", "AI Deployment Activated", "Model deployment activated"),
            new SeedEvent("AI_DEPLOYMENT_DEACTIVATED", "AI Deployment Deactivated", "Model deployment deactivated"),
            new SeedEvent("AI_AGENT_CREATED", "AI Agent Created", "Agent created"),
            new SeedEvent("AI_AGENT_UPDATED", "AI Agent Updated", "Agent updated"),
            new SeedEvent("AI_AGENT_ACTIVATED", "AI Agent Activated", "Agent activated"),
            new SeedEvent("AI_AGENT_DEACTIVATED", "AI Agent Deactivated", "Agent deactivated"),
            new SeedEvent("AI_PROMPT_TEMPLATE_CREATED", "AI Prompt Template Created", "Prompt template created"),
            new SeedEvent("AI_PROMPT_VERSION_CREATED", "AI Prompt Version Created", "Prompt version created"),
            new SeedEvent("AI_PROMPT_VERSION_ACTIVATED", "AI Prompt Version Activated", "Prompt version activated"),
            new SeedEvent("AI_EVENT_CONFIG_CREATED", "AI Event Config Created", "Event config created"),
            new SeedEvent("AI_EVENT_CONFIG_ACTIVATED", "AI Event Config Activated", "Event config activated"),
            new SeedEvent("AI_EVENT_CONFIG_DEACTIVATED", "AI Event Config Deactivated", "Event config deactivated"),
            new SeedEvent("AI_EXECUTION_STARTED", "AI Execution Started", "Execution started"),
            new SeedEvent("AI_EXECUTION_SUCCEEDED", "AI Execution Succeeded", "Execution succeeded"),
            new SeedEvent("AI_EXECUTION_FAILED", "AI Execution Failed", "Execution failed"),
            new SeedEvent("AI_USAGE_POLICY_CREATED", "AI Usage Policy Created", "Usage policy created"),
            new SeedEvent("AI_USAGE_POLICY_ACTIVATED", "AI Usage Policy Activated", "Usage policy activated"),
            new SeedEvent("AI_USAGE_POLICY_BLOCKED", "AI Usage Policy Blocked", "Usage policy blocked an execution")
    );
}
