package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.enums.ContextStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiContextSnapshot;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiContextSnapshotJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AiContextSnapshotPersistenceMapper {

    private final ObjectMapper objectMapper;

    public AiContextSnapshotPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiContextSnapshotJpaEntity toJpaEntity(AiContextSnapshot domain) {
        AiContextSnapshotJpaEntity entity = new AiContextSnapshotJpaEntity();
        entity.setId(domain.id());
        entity.setConversationId(domain.conversationId());
        entity.setAssistantMessageId(domain.assistantMessageId());
        entity.setTurnId(domain.turnId());
        entity.setActorId(domain.actorId());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setRoute(domain.route());
        entity.setPageCode(domain.pageCode());
        entity.setPageMetadataVersion(domain.pageMetadataVersion());
        entity.setEntityType(domain.entityType());
        entity.setEntityId(domain.entityId());
        entity.setEntityVersion(domain.entityVersion());
        entity.setSelectedActionCode(domain.selectedActionCode());
        entity.setTabCode(domain.tabCode());
        entity.setLocale(domain.locale());
        entity.setTimezone(domain.timezone());
        entity.setClientContextVersion(domain.clientContextVersion());
        entity.setClientVisibleFieldCodes(toJson(domain.clientVisibleFieldCodes()));
        entity.setClientReportedActionCodes(toJson(domain.clientReportedActionCodes()));
        entity.setServerVisibleFieldCodes(toJson(domain.serverVisibleFieldCodes()));
        entity.setAvailableActionCodes(toJson(domain.availableActionCodes()));
        entity.setDisabledActionReasons(domain.disabledActionReasonsJson());
        entity.setPermissionSignature(domain.permissionSignature());
        entity.setClientContextHash(domain.clientContextHash());
        entity.setContextHash(domain.contextHash());
        entity.setServerContext(domain.serverContextJson());
        entity.setContextStatus(domain.contextStatus() != null ? domain.contextStatus().name() : null);
        entity.setInvalidationReasonCode(domain.invalidationReasonCode());
        entity.setCreatedAt(domain.createdAt());
        entity.setExpiresAt(domain.expiresAt());
        entity.setInvalidatedAt(domain.invalidatedAt());
        return entity;
    }

    public AiContextSnapshot toDomain(AiContextSnapshotJpaEntity entity) {
        return AiContextSnapshot.reconstitute(
                entity.getId(),
                entity.getConversationId(),
                entity.getAssistantMessageId(),
                entity.getTurnId(),
                entity.getActorId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getRoute(),
                entity.getPageCode(),
                entity.getPageMetadataVersion(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getEntityVersion(),
                entity.getSelectedActionCode(),
                entity.getTabCode(),
                entity.getLocale(),
                entity.getTimezone(),
                entity.getClientContextVersion(),
                fromJson(entity.getClientVisibleFieldCodes()),
                fromJson(entity.getClientReportedActionCodes()),
                fromJson(entity.getServerVisibleFieldCodes()),
                fromJson(entity.getAvailableActionCodes()),
                entity.getDisabledActionReasons(),
                entity.getPermissionSignature(),
                entity.getClientContextHash(),
                entity.getContextHash(),
                entity.getServerContext(),
                entity.getContextStatus() != null ? ContextStatus.valueOf(entity.getContextStatus()) : null,
                entity.getInvalidationReasonCode(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getInvalidatedAt()
        );
    }

    private String toJson(List<String> list) {
        if (list == null) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
