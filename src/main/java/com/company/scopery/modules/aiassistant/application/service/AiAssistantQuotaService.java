package com.company.scopery.modules.aiassistant.application.service;

import com.company.scopery.modules.aiassistant.domain.enums.ActiveStreamStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiActiveStreamRepository;
import com.company.scopery.modules.aiassistant.domain.model.AiQuotaUsage;
import com.company.scopery.modules.aiassistant.domain.model.AiQuotaUsageRepository;
import com.company.scopery.modules.aiassistant.shared.config.AiAssistantProperties;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AiAssistantQuotaService {

    private final AiQuotaUsageRepository quotaUsageRepository;
    private final AiActiveStreamRepository activeStreamRepository;
    private final AiAssistantProperties properties;

    public AiAssistantQuotaService(AiQuotaUsageRepository quotaUsageRepository,
                                   AiActiveStreamRepository activeStreamRepository,
                                   AiAssistantProperties properties) {
        this.quotaUsageRepository = quotaUsageRepository;
        this.activeStreamRepository = activeStreamRepository;
        this.properties = properties;
    }

    @Transactional
    public void reserveTurn(UUID actorId, UUID workspaceId) {
        AiQuotaUsage usage = quotaUsageRepository
                .findByWorkspaceIdAndActorIdAndUsageDateForUpdate(workspaceId, actorId, LocalDate.now())
                .orElseGet(() -> {
                    AiQuotaUsage newUsage = AiQuotaUsage.createForToday(workspaceId, actorId);
                    return quotaUsageRepository.save(newUsage);
                });

        if (usage.turnCount() >= properties.getMaxTurnsPerUserPerDay()) {
            throw AiAssistantExceptions.quotaDailyTurnsExceeded(actorId, properties.getMaxTurnsPerUserPerDay());
        }

        long totalTokens = usage.inputTokenCount() + usage.outputTokenCount();
        if (totalTokens >= properties.getMaxTokensPerUserPerDay()) {
            throw AiAssistantExceptions.quotaDailyTokensExceeded(actorId, properties.getMaxTokensPerUserPerDay());
        }

        int activeStreams = activeStreamRepository
                .countByWorkspaceIdAndActorIdAndStreamStatus(workspaceId, actorId, ActiveStreamStatus.ACTIVE);
        if (activeStreams >= properties.getMaxActiveStreamsPerUser()) {
            throw AiAssistantExceptions.quotaActiveStreamsExceeded(actorId, properties.getMaxActiveStreamsPerUser());
        }

        usage.incrementTurn();
        quotaUsageRepository.save(usage);
    }

    @Transactional
    public void finalizeQuota(UUID actorId, UUID workspaceId,
                               int inputTokens, int outputTokens,
                               boolean failed, boolean blocked) {
        AiQuotaUsage usage = quotaUsageRepository
                .findByWorkspaceIdAndActorIdAndUsageDate(workspaceId, actorId, LocalDate.now())
                .orElseGet(() -> AiQuotaUsage.createForToday(workspaceId, actorId));

        usage.addTokens(inputTokens, outputTokens);
        if (failed) {
            usage.incrementFailedTurn();
        }
        if (blocked) {
            usage.incrementBlockedTurn();
        }
        quotaUsageRepository.save(usage);
    }
}
