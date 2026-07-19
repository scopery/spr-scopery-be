package com.company.scopery.modules.aiassistant.application.service;

import com.company.scopery.modules.aiassistant.domain.enums.MemorySummaryStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiMemorySummary;
import com.company.scopery.modules.aiassistant.domain.model.AiMemorySummaryRepository;
import com.company.scopery.modules.aiassistant.shared.config.AiAssistantProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class AiAssistantMemorySummaryService {

    private final AiMemorySummaryRepository memorySummaryRepository;
    private final AiAssistantProperties properties;

    public AiAssistantMemorySummaryService(AiMemorySummaryRepository memorySummaryRepository,
                                           AiAssistantProperties properties) {
        this.memorySummaryRepository = memorySummaryRepository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public boolean needsSummarization(UUID conversationId, int currentTurnCount) {
        int triggerTurns = properties.getMemory().getTriggerTurns();
        return triggerTurns > 0 && currentTurnCount % triggerTurns == 0;
    }

    @Transactional
    public Optional<AiMemorySummary> getActiveSummary(UUID conversationId) {
        return memorySummaryRepository.findByConversationIdAndStatus(
                conversationId, MemorySummaryStatus.ACTIVE);
    }

    @Transactional
    public void supersedePreviousIfAny(UUID conversationId) {
        memorySummaryRepository.findByConversationIdAndStatus(conversationId, MemorySummaryStatus.ACTIVE)
                .ifPresent(summary -> {
                    summary.supersede();
                    memorySummaryRepository.save(summary);
                });
    }
}
