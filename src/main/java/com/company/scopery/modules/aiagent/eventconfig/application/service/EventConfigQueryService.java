package com.company.scopery.modules.aiagent.eventconfig.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.application.query.GetEventConfigDetailQuery;
import com.company.scopery.modules.aiagent.eventconfig.application.query.ResolveActiveEventConfigQuery;
import com.company.scopery.modules.aiagent.eventconfig.application.query.SearchEventConfigQuery;
import com.company.scopery.modules.aiagent.eventconfig.application.response.EventConfigDetailResponse;
import com.company.scopery.modules.aiagent.eventconfig.application.response.EventConfigResponse;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventTriggerType;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventConfigQueryService {

    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final String runtimeEnvironment;

    public EventConfigQueryService(EventConfigRepository eventConfigRepository,
                                   EventDefinitionRepository eventDefinitionRepository,
                                   AgentRepository agentRepository,
                                   PromptVersionRepository promptVersionRepository,
                                   PromptTemplateRepository promptTemplateRepository,
                                   ModelDeploymentRepository modelDeploymentRepository,
                                   @Value("${scopery.aiagent.runtime-environment}")
                                   String runtimeEnvironment) {
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    @Transactional(readOnly = true)
    public EventConfigDetailResponse getEventConfigDetail(GetEventConfigDetailQuery query) {
        EventConfig config = findOrThrow(query.id());
        return buildDetailResponse(config);
    }

    @Transactional(readOnly = true)
    public PageResult<EventConfigResponse> searchEventConfigs(SearchEventConfigQuery query) {
        EventConfigEnvironment environment = AiAgentEnumParser.parseOptional(
                EventConfigEnvironment.class, query.environment(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");
        EventTriggerType triggerType = AiAgentEnumParser.parseOptional(
                EventTriggerType.class, query.triggerType(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_TRIGGER_TYPE.code(), "triggerType");
        EventConfigStatus status = AiAgentEnumParser.parseOptional(
                EventConfigStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return eventConfigRepository
                .findAll(query.keyword(), query.eventDefinitionId(), environment,
                        triggerType, status, query.agentId(), pageQuery)
                .map(EventConfigResponse::from);
    }

    @Transactional(readOnly = true)
    public EventConfigDetailResponse resolveActiveEventConfig(ResolveActiveEventConfigQuery query) {
        UUID eventDefinitionId = resolveEventDefinitionId(query);
        EventConfigEnvironment environment = parseEnvironment(query.environment());

        EventConfig config = eventConfigRepository
                .findActiveByEventDefinitionIdAndEnvironment(eventDefinitionId, environment)
                .orElseThrow(() -> AiAgentExceptions.activeEventConfigNotFound(
                        eventDefinitionId, environment.name()));

        return buildDetailResponse(config);
    }

    private UUID resolveEventDefinitionId(ResolveActiveEventConfigQuery query) {
        if (query.eventDefinitionId() != null) {
            return query.eventDefinitionId();
        }
        String sourceSystem = query.sourceSystem();
        String eventKey = query.eventKey();
        if (sourceSystem == null || sourceSystem.isBlank() || eventKey == null || eventKey.isBlank()) {
            throw AiAgentExceptions.eventConfigResolveIdentifierRequired();
        }
        SourceSystemCode sc = SourceSystemCode.of(sourceSystem);
        EventKey ek = EventKey.of(eventKey);
        EventDefinition ed = eventDefinitionRepository.findBySourceSystemAndEventKey(sc, ek)
                .orElseThrow(() -> AiAgentExceptions.eventConfigEventDefinitionNotFound(null));
        return ed.id();
    }

    private EventConfigEnvironment parseEnvironment(String raw) {
        String resolved = (raw == null || raw.isBlank()) ? runtimeEnvironment : raw;
        return AiAgentEnumParser.parseRequired(
                EventConfigEnvironment.class, resolved,
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");
    }

    private EventConfig findOrThrow(UUID id) {
        return eventConfigRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.eventConfigNotFound(id));
    }

    private EventConfigDetailResponse buildDetailResponse(EventConfig config) {
        EventDefinition eventDef = eventDefinitionRepository.findById(config.eventDefinitionId()).orElse(null);
        String eventDefinitionCode = eventDef != null ? eventDef.code().value() : null;
        String sourceSystem = eventDef != null ? eventDef.sourceSystem().value() : null;
        String eventKey = eventDef != null ? eventDef.eventKey().value() : null;

        Agent agent = agentRepository.findById(config.agentId()).orElse(null);
        String agentName = agent != null ? agent.name() : null;

        PromptVersion promptVersion = promptVersionRepository.findById(config.promptVersionId()).orElse(null);
        Integer promptVersionNumber = promptVersion != null ? promptVersion.versionNumber() : null;
        String promptTemplateCode = null;
        if (promptVersion != null) {
            PromptTemplate template = promptTemplateRepository.findById(promptVersion.templateId()).orElse(null);
            promptTemplateCode = template != null ? template.code().value() : null;
        }

        ModelDeployment deployment = modelDeploymentRepository.findById(config.modelDeploymentId()).orElse(null);
        String deploymentCode = deployment != null ? deployment.code().value() : null;
        String deploymentName = deployment != null ? deployment.name() : null;

        return EventConfigDetailResponse.from(config, eventDefinitionCode, sourceSystem, eventKey,
                agentName, promptTemplateCode, promptVersionNumber, deploymentCode, deploymentName);
    }
}
