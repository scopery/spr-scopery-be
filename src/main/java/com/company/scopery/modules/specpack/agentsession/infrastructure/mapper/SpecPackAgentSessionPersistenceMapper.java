package com.company.scopery.modules.specpack.agentsession.infrastructure.mapper;

import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentSessionStatus;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageCode;
import com.company.scopery.modules.specpack.agentsession.domain.enums.AgentStageStatus;
import com.company.scopery.modules.specpack.agentsession.domain.enums.ReadinessStatus;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentSession;
import com.company.scopery.modules.specpack.agentsession.domain.model.SpecPackAgentStage;
import com.company.scopery.modules.specpack.agentsession.infrastructure.persistence.SpecPackAgentSessionJpaEntity;
import com.company.scopery.modules.specpack.agentsession.infrastructure.persistence.SpecPackAgentStageJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpecPackAgentSessionPersistenceMapper {

    private static final Logger log = LoggerFactory.getLogger(SpecPackAgentSessionPersistenceMapper.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public SpecPackAgentSessionPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SpecPackAgentSessionJpaEntity toJpaEntity(SpecPackAgentSession domain) {
        SpecPackAgentSessionJpaEntity entity = new SpecPackAgentSessionJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setSpecPackId(domain.specPackId());
        entity.setScopePackageId(domain.scopePackageId());
        entity.setStatus(domain.status().name());
        entity.setCurrentStageCode(domain.currentStageCode().name());
        entity.setReadinessStatus(domain.readinessStatus().name());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public SpecPackAgentSession toDomain(SpecPackAgentSessionJpaEntity entity) {
        return SpecPackAgentSession.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getSpecPackId(),
                entity.getScopePackageId(),
                AgentSessionStatus.valueOf(entity.getStatus()),
                AgentStageCode.valueOf(entity.getCurrentStageCode()),
                ReadinessStatus.valueOf(entity.getReadinessStatus()),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public SpecPackAgentStageJpaEntity toStageJpaEntity(SpecPackAgentStage domain) {
        SpecPackAgentStageJpaEntity entity = new SpecPackAgentStageJpaEntity();
        entity.setId(domain.id());
        entity.setSessionId(domain.sessionId());
        entity.setStageCode(domain.stageCode().name());
        entity.setStageStatus(domain.stageStatus().name());
        entity.setResultJson(toJson(domain.resultJson()));
        entity.setCompletedAt(domain.completedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public SpecPackAgentStage toStageDomain(SpecPackAgentStageJpaEntity entity) {
        return SpecPackAgentStage.reconstitute(
                (java.util.UUID) entity.getId(),
                entity.getSessionId(),
                AgentStageCode.valueOf(entity.getStageCode()),
                AgentStageStatus.valueOf(entity.getStageStatus()),
                fromJsonMap(entity.getResultJson()),
                entity.getCompletedAt(),
                entity.getCreatedAt()
        );
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize to JSON", e);
            return null;
        }
    }

    private Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception e) {
            log.warn("Failed to deserialize result JSON", e);
            return null;
        }
    }
}
