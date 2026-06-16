package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.mapper;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.*;
import com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence.EventDefinitionJpaEntity;
import com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence.EventVariableJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EventDefinitionPersistenceMapper {

    public EventDefinitionJpaEntity toJpaEntity(EventDefinition domain) {
        EventDefinitionJpaEntity entity = new EventDefinitionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code().value());
        entity.setName(domain.name());
        entity.setSourceSystem(domain.sourceSystem().value());
        entity.setEventKey(domain.eventKey().value());
        entity.setDescription(domain.description());
        entity.setInputSchema(domain.inputSchema());
        entity.setOutputSchema(domain.outputSchema());
        entity.setStatus(domain.status().name());
        entity.setEventVersion(domain.eventVersion());
        entity.setSamplePayloadJson(domain.samplePayloadJson());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public EventDefinition toDomain(EventDefinitionJpaEntity entity) {
        return EventDefinition.reconstitute(
                entity.getId(),
                EventDefinitionCode.of(entity.getCode()),
                entity.getName(),
                SourceSystemCode.of(entity.getSourceSystem()),
                EventKey.of(entity.getEventKey()),
                entity.getDescription(),
                entity.getInputSchema(),
                entity.getOutputSchema(),
                EventDefinitionStatus.valueOf(entity.getStatus()),
                entity.getEventVersion(),
                entity.getSamplePayloadJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public EventVariableJpaEntity toVariableJpaEntity(EventVariable domain) {
        EventVariableJpaEntity entity = new EventVariableJpaEntity();
        entity.setId(domain.id());
        entity.setEventDefinitionId(domain.eventDefinitionId());
        entity.setVariablePath(domain.variablePath());
        entity.setVariableLabel(domain.variableLabel());
        entity.setVariableType(domain.variableType().name());
        entity.setRequired(domain.required());
        entity.setDescription(domain.description());
        entity.setExampleValue(domain.exampleValue());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }

    public EventVariable toVariableDomain(EventVariableJpaEntity entity) {
        return EventVariable.reconstitute(
                entity.getId(),
                entity.getEventDefinitionId(),
                entity.getVariablePath(),
                entity.getVariableLabel(),
                VariableType.valueOf(entity.getVariableType()),
                entity.isRequired(),
                entity.getDescription(),
                entity.getExampleValue(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
