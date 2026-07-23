package com.company.scopery.modules.traceability.funcitemprop.infrastructure.mapper;

import com.company.scopery.modules.traceability.funcitemprop.domain.enums.CustomPropertyFieldType;
import com.company.scopery.modules.traceability.funcitemprop.domain.model.FunctionalItemCustomProperty;
import com.company.scopery.modules.traceability.funcitemprop.infrastructure.persistence.FunctionalItemCustomPropertyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FunctionalItemCustomPropertyPersistenceMapper {

    public FunctionalItemCustomProperty toDomain(FunctionalItemCustomPropertyJpaEntity e) {
        return new FunctionalItemCustomProperty(
                e.getId(),
                e.getFunctionalItemId(),
                e.getPropKey(),
                e.getPropValue(),
                e.getFieldType() != null ? CustomPropertyFieldType.valueOf(e.getFieldType()) : null,
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public FunctionalItemCustomPropertyJpaEntity toJpaEntity(FunctionalItemCustomProperty d) {
        FunctionalItemCustomPropertyJpaEntity e = new FunctionalItemCustomPropertyJpaEntity();
        e.setId(d.id());
        e.setFunctionalItemId(d.functionalItemId());
        e.setPropKey(d.propKey());
        e.setPropValue(d.propValue());
        e.setFieldType(d.fieldType() != null ? d.fieldType().name() : null);
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}
