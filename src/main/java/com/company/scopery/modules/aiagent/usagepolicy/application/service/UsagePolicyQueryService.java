package com.company.scopery.modules.aiagent.usagepolicy.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.aiagent.usagepolicy.application.query.GetUsagePolicyDetailQuery;
import com.company.scopery.modules.aiagent.usagepolicy.application.query.SearchUsagePolicyQuery;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.UsagePolicyDetailResponse;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.UsagePolicyResponse;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyStatus;
import com.company.scopery.modules.aiagent.usagepolicy.domain.enums.UsagePolicyTargetType;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicy;
import com.company.scopery.modules.aiagent.usagepolicy.domain.model.UsagePolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UsagePolicyQueryService {

    private final UsagePolicyRepository usagePolicyRepository;
    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;

    public UsagePolicyQueryService(UsagePolicyRepository usagePolicyRepository,
                                   EventConfigRepository eventConfigRepository,
                                   AgentRepository agentRepository,
                                   ModelDeploymentRepository modelDeploymentRepository) {
        this.usagePolicyRepository = usagePolicyRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.agentRepository = agentRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
    }

    @Transactional(readOnly = true)
    public UsagePolicyDetailResponse getUsagePolicyDetail(GetUsagePolicyDetailQuery query) {
        UsagePolicy policy = usagePolicyRepository.findById(query.id())
                .orElseThrow(() -> AiAgentExceptions.usagePolicyNotFound(query.id()));
        return buildDetailResponse(policy);
    }

    @Transactional(readOnly = true)
    public PageResult<UsagePolicyResponse> searchUsagePolicies(SearchUsagePolicyQuery query) {
        UsagePolicyTargetType targetType = AiAgentEnumParser.parseOptional(
                UsagePolicyTargetType.class, query.targetType(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_TARGET_TYPE.code(), "targetType");
        UsagePolicyStatus status = AiAgentEnumParser.parseOptional(
                UsagePolicyStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return usagePolicyRepository
                .findAll(query.keyword(), targetType, status, pageQuery)
                .map(UsagePolicyResponse::from);
    }

    private UsagePolicyDetailResponse buildDetailResponse(UsagePolicy policy) {
        String targetName = resolveTargetName(policy.targetType(), policy.targetId());
        return UsagePolicyDetailResponse.from(policy, targetName);
    }

    private String resolveTargetName(UsagePolicyTargetType targetType, UUID targetId) {
        return switch (targetType) {
            case GLOBAL -> "Global";
            case EVENT_CONFIG -> eventConfigRepository.findById(targetId)
                    .map(EventConfig::code).map(c -> c.value()).orElse(null);
            case AGENT -> agentRepository.findById(targetId)
                    .map(Agent::name).orElse(null);
            case MODEL_DEPLOYMENT -> modelDeploymentRepository.findById(targetId)
                    .map(ModelDeployment::name).orElse(null);
        };
    }
}
