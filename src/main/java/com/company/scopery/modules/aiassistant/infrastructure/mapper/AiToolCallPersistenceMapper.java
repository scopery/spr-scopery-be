package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.enums.ToolCallStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiToolCallRecord;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiToolCallJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiToolCallPersistenceMapper {

    public AiToolCallJpaEntity toJpaEntity(AiToolCallRecord domain) {
        AiToolCallJpaEntity entity = new AiToolCallJpaEntity();
        entity.setId(domain.id());
        entity.setConversationId(domain.conversationId());
        entity.setTurnId(domain.turnId());
        entity.setRequestMessageId(domain.requestMessageId());
        entity.setResultMessageId(domain.resultMessageId());
        entity.setToolCode(domain.toolCode());
        entity.setToolVersion(domain.toolVersion());
        entity.setHandlerCode(domain.handlerCode());
        entity.setStatus(domain.status() != null ? domain.status().name() : null);
        entity.setRequestHash(domain.requestHash());
        entity.setMaskedArguments(domain.maskedArgumentsJson());
        entity.setServerResolvedScope(domain.serverResolvedScopeJson());
        entity.setResultSummary(domain.resultSummaryJson());
        entity.setRetrievalTraceId(domain.retrievalTraceId());
        entity.setResultCount(domain.resultCount());
        entity.setTruncated(domain.truncated());
        entity.setLatencyMs(domain.latencyMs());
        entity.setErrorCode(domain.errorCode());
        entity.setErrorSummaryRedacted(domain.errorSummaryRedacted());
        entity.setCreatedAt(domain.createdAt());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        return entity;
    }

    public AiToolCallRecord toDomain(AiToolCallJpaEntity entity) {
        return AiToolCallRecord.reconstitute(
                entity.getId(),
                entity.getConversationId(),
                entity.getTurnId(),
                entity.getRequestMessageId(),
                entity.getResultMessageId(),
                entity.getToolCode(),
                entity.getToolVersion(),
                entity.getHandlerCode(),
                entity.getStatus() != null ? ToolCallStatus.valueOf(entity.getStatus()) : null,
                entity.getRequestHash(),
                entity.getMaskedArguments(),
                entity.getServerResolvedScope(),
                entity.getResultSummary(),
                entity.getRetrievalTraceId(),
                entity.getResultCount(),
                entity.isTruncated(),
                entity.getLatencyMs(),
                entity.getErrorCode(),
                entity.getErrorSummaryRedacted(),
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }
}
