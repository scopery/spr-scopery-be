package com.company.scopery.modules.aiplanning.applyresult.infrastructure.mapper;

import com.company.scopery.modules.aiplanning.applyresult.domain.enums.ApplyResultStatus;
import com.company.scopery.modules.aiplanning.applyresult.domain.model.AiPlanningApplyResult;
import com.company.scopery.modules.aiplanning.applyresult.infrastructure.persistence.AiPlanningApplyResultJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiPlanningApplyResultPersistenceMapper {
    public AiPlanningApplyResult toDomain(AiPlanningApplyResultJpaEntity e) {
        return new AiPlanningApplyResult(e.getId(), e.getSuggestionId(), e.getSuggestionItemId(), e.getProjectId(),
                ApplyResultStatus.valueOf(e.getStatus()), e.getDomainAction(), e.getTargetType(), e.getTargetId(),
                e.getResultPayloadJson(), e.getErrorCode(), e.getErrorMessage(), e.getCreatedBy(), e.getTraceId(), e.getCreatedAt());
    }
    public AiPlanningApplyResultJpaEntity toJpaEntity(AiPlanningApplyResult d) {
        AiPlanningApplyResultJpaEntity e = new AiPlanningApplyResultJpaEntity();
        e.setId(d.id()); e.setSuggestionId(d.suggestionId()); e.setSuggestionItemId(d.suggestionItemId());
        e.setProjectId(d.projectId()); e.setStatus(d.status().name()); e.setDomainAction(d.domainAction());
        e.setTargetType(d.targetType()); e.setTargetId(d.targetId()); e.setResultPayloadJson(d.resultPayloadJson());
        e.setErrorCode(d.errorCode()); e.setErrorMessage(d.errorMessage()); e.setCreatedBy(d.createdBy());
        e.setTraceId(d.traceId()); e.setCreatedAt(d.createdAt());
        return e;
    }
}
