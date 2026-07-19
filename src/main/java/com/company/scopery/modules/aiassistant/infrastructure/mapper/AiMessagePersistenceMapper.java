package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.enums.ContentFormat;
import com.company.scopery.modules.aiassistant.domain.enums.MessageRole;
import com.company.scopery.modules.aiassistant.domain.enums.MessageStatus;
import com.company.scopery.modules.aiassistant.domain.enums.ResponseMode;
import com.company.scopery.modules.aiassistant.domain.model.AiMessage;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiMessageJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiMessagePersistenceMapper {

    public AiMessageJpaEntity toJpaEntity(AiMessage domain) {
        AiMessageJpaEntity entity = new AiMessageJpaEntity();
        entity.setId(domain.id());
        entity.setConversationId(domain.conversationId());
        entity.setTurnId(domain.turnId());
        entity.setParentMessageId(domain.parentMessageId());
        entity.setIdempotencyKey(domain.idempotencyKey());
        entity.setSequenceInConversation(domain.sequenceInConversation());
        entity.setRole(domain.role() != null ? domain.role().name() : null);
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setContentFormat(domain.contentFormat() != null ? domain.contentFormat().name() : null);
        entity.setContent(domain.content());
        entity.setResponseMode(domain.responseMode() != null ? domain.responseMode().name() : null);
        entity.setModelProvider(domain.modelProvider());
        entity.setModelName(domain.modelName());
        entity.setModelDeployment(domain.modelDeployment());
        entity.setPromptProfileCode(domain.promptProfileCode());
        entity.setInputTokenCount(domain.inputTokenCount());
        entity.setOutputTokenCount(domain.outputTokenCount());
        entity.setLatencyMs(domain.latencyMs());
        entity.setFinishReason(domain.finishReason());
        entity.setErrorCode(domain.errorCode());
        entity.setErrorSummaryRedacted(domain.errorSummaryRedacted());
        entity.setTraceId(domain.traceId());
        entity.setCorrelationId(domain.correlationId());
        entity.setCancelRequestedAt(domain.cancelRequestedAt());
        entity.setCancelRequestedBy(domain.cancelRequestedBy());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        entity.setCancelledAt(domain.cancelledAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }

    public AiMessage toDomain(AiMessageJpaEntity entity) {
        return AiMessage.reconstitute(
                entity.getId(),
                entity.getConversationId(),
                entity.getTurnId(),
                entity.getParentMessageId(),
                entity.getIdempotencyKey(),
                entity.getSequenceInConversation(),
                entity.getRole() != null ? MessageRole.valueOf(entity.getRole()) : null,
                entity.getStatus() != null ? MessageStatus.valueOf(entity.getStatus()) : null,
                entity.getContentFormat() != null ? ContentFormat.valueOf(entity.getContentFormat()) : null,
                entity.getContent(),
                entity.getResponseMode() != null ? ResponseMode.valueOf(entity.getResponseMode()) : null,
                entity.getModelProvider(),
                entity.getModelName(),
                entity.getModelDeployment(),
                entity.getPromptProfileCode(),
                entity.getInputTokenCount(),
                entity.getOutputTokenCount(),
                entity.getLatencyMs(),
                entity.getFinishReason(),
                entity.getErrorCode(),
                entity.getErrorSummaryRedacted(),
                entity.getTraceId(),
                entity.getCorrelationId(),
                entity.getCancelRequestedAt(),
                entity.getCancelRequestedBy(),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getCancelledAt(),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion()
        );
    }

    private static UUID parseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
