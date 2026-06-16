package com.company.scopery.modules.aiagent.usagepolicy.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.agent.domain.Agent;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.aiagent.usagepolicy.application.command.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.query.*;
import com.company.scopery.modules.aiagent.usagepolicy.application.response.*;
import com.company.scopery.modules.aiagent.usagepolicy.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UsagePolicyApplicationService {

    private static final int DEFAULT_PRIORITY = 100;

    private final UsagePolicyRepository usagePolicyRepository;
    private final EventConfigRepository eventConfigRepository;
    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public UsagePolicyApplicationService(UsagePolicyRepository usagePolicyRepository,
                                         EventConfigRepository eventConfigRepository,
                                         AgentRepository agentRepository,
                                         ModelDeploymentRepository modelDeploymentRepository,
                                         AiAgentActivityLogger activityLogger) {
        this.usagePolicyRepository = usagePolicyRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.agentRepository = agentRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UsagePolicyResponse createUsagePolicy(CreateUsagePolicyCommand command) {
        UsagePolicyCode code = UsagePolicyCode.of(command.code());
        if (usagePolicyRepository.existsByCode(code)) {
            throw AiAgentExceptions.usagePolicyCodeAlreadyExists(code.value());
        }

        UsagePolicyTargetType targetType = AiAgentEnumParser.parseRequired(
                UsagePolicyTargetType.class, command.targetType(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_TARGET_TYPE.code(), "targetType");

        validateTargetConsistency(targetType, command.targetId());
        validateLimits(command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(),
                command.maxCostPerPeriod(), command.maxConcurrentRequests(),
                command.dailyBudget(), command.period());

        UsagePolicyPeriod period = AiAgentEnumParser.parseOptional(
                UsagePolicyPeriod.class, command.period(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_PERIOD.code(), "period");
        UsagePolicyAction action = AiAgentEnumParser.parseRequired(
                UsagePolicyAction.class, command.action(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_ACTION.code(), "action");

        validateTargetExists(targetType, command.targetId());

        if (usagePolicyRepository.existsActiveByTargetTypeAndTargetId(targetType, command.targetId(), null)) {
            throw AiAgentExceptions.usagePolicyActiveAlreadyExists(targetType.name(), command.targetId());
        }

        int priority = command.priority() != null ? command.priority() : DEFAULT_PRIORITY;

        UsagePolicy policy = UsagePolicy.create(code, command.name(), targetType, command.targetId(),
                command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(), command.maxCostPerPeriod(),
                command.maxConcurrentRequests(), command.dailyBudget(), period, action, priority,
                command.description());

        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.CREATE_USAGE_POLICY,
                "Usage policy created: " + saved.code().value()
                        + " targetType=" + saved.targetType().name());

        return UsagePolicyResponse.from(saved);
    }

    @Transactional
    public UsagePolicyDetailResponse updateUsagePolicy(UpdateUsagePolicyCommand command) {
        UsagePolicy policy = findOrThrow(command.id());

        validateLimits(command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(),
                command.maxCostPerPeriod(), command.maxConcurrentRequests(),
                command.dailyBudget(), command.period());

        UsagePolicyPeriod period = AiAgentEnumParser.parseOptional(
                UsagePolicyPeriod.class, command.period(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_PERIOD.code(), "period");
        UsagePolicyAction action = AiAgentEnumParser.parseRequired(
                UsagePolicyAction.class, command.action(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_ACTION.code(), "action");

        int priority = command.priority() != null ? command.priority() : DEFAULT_PRIORITY;

        policy.update(command.name(), command.maxRequestsPerPeriod(), command.maxTokensPerPeriod(),
                command.maxCostPerPeriod(), command.maxConcurrentRequests(), command.dailyBudget(),
                period, action, priority, command.description());

        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.UPDATE_USAGE_POLICY,
                "Usage policy updated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public UsagePolicyDetailResponse getUsagePolicyDetail(GetUsagePolicyDetailQuery query) {
        UsagePolicy policy = findOrThrow(query.id());
        return buildDetailResponse(policy);
    }

    @Transactional(readOnly = true)
    public Page<UsagePolicyResponse> searchUsagePolicies(SearchUsagePolicyQuery query) {
        UsagePolicyTargetType targetType = AiAgentEnumParser.parseOptional(
                UsagePolicyTargetType.class, query.targetType(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_TARGET_TYPE.code(), "targetType");
        UsagePolicyStatus status = AiAgentEnumParser.parseOptional(
                UsagePolicyStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_USAGE_POLICY_STATUS.code(), "status");

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return usagePolicyRepository
                .findAll(query.keyword(), targetType, status, pageable)
                .map(UsagePolicyResponse::from);
    }

    @Transactional
    public UsagePolicyDetailResponse activateUsagePolicy(ActivateUsagePolicyCommand command) {
        UsagePolicy policy = findOrThrow(command.id());

        if (policy.status() == UsagePolicyStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedUsagePolicyCannotBeActivated(policy.code().value());
        }

        if (usagePolicyRepository.existsActiveByTargetTypeAndTargetId(
                policy.targetType(), policy.targetId(), policy.id())) {
            throw AiAgentExceptions.usagePolicyActiveAlreadyExists(
                    policy.targetType().name(), policy.targetId());
        }

        policy.activate();
        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.ACTIVATE_USAGE_POLICY,
                "Usage policy activated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    @Transactional
    public UsagePolicyDetailResponse deactivateUsagePolicy(DeactivateUsagePolicyCommand command) {
        UsagePolicy policy = findOrThrow(command.id());
        policy.deactivate();
        UsagePolicy saved = usagePolicyRepository.save(policy);

        activityLogger.logSuccess(AiAgentEntityTypes.USAGE_POLICY, saved.id(),
                AiAgentActivityActions.DEACTIVATE_USAGE_POLICY,
                "Usage policy deactivated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void validateTargetConsistency(UsagePolicyTargetType targetType, UUID targetId) {
        if (targetType == UsagePolicyTargetType.GLOBAL) {
            if (targetId != null) {
                throw AiAgentExceptions.usagePolicyTargetIdMustBeNull();
            }
        } else {
            if (targetId == null) {
                throw AiAgentExceptions.usagePolicyTargetIdRequired(targetType.name());
            }
        }
    }

    private void validateLimits(Integer maxRequestsPerPeriod, Long maxTokensPerPeriod,
                                 java.math.BigDecimal maxCostPerPeriod, Integer maxConcurrentRequests,
                                 java.math.BigDecimal dailyBudget, String periodRaw) {
        boolean hasAnyLimit = maxRequestsPerPeriod != null || maxTokensPerPeriod != null
                || maxCostPerPeriod != null || maxConcurrentRequests != null || dailyBudget != null;
        if (!hasAnyLimit) {
            throw AiAgentExceptions.usagePolicyNoLimitDefined();
        }
        boolean hasPeriodBasedLimit = maxRequestsPerPeriod != null
                || maxTokensPerPeriod != null || maxCostPerPeriod != null;
        if (hasPeriodBasedLimit && (periodRaw == null || periodRaw.isBlank())) {
            throw AiAgentExceptions.usagePolicyPeriodRequired();
        }
    }

    private void validateTargetExists(UsagePolicyTargetType targetType, UUID targetId) {
        if (targetType == UsagePolicyTargetType.GLOBAL) return;
        switch (targetType) {
            case EVENT_CONFIG -> {
                if (eventConfigRepository.findById(targetId).isEmpty()) {
                    throw AiAgentExceptions.usagePolicyEventConfigNotFound(targetId);
                }
            }
            case AGENT -> {
                if (agentRepository.findById(targetId).isEmpty()) {
                    throw AiAgentExceptions.usagePolicyAgentNotFound(targetId);
                }
            }
            case MODEL_DEPLOYMENT -> {
                if (modelDeploymentRepository.findById(targetId).isEmpty()) {
                    throw AiAgentExceptions.usagePolicyModelDeploymentNotFound(targetId);
                }
            }
        }
    }

    private UsagePolicy findOrThrow(UUID id) {
        return usagePolicyRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.usagePolicyNotFound(id));
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