package com.company.scopery.modules.traceability.functionapi.infrastructure.mapper;

import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;
import com.company.scopery.modules.traceability.functionapi.infrastructure.persistence.FunctionApiId;
import com.company.scopery.modules.traceability.functionapi.infrastructure.persistence.FunctionApiJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FunctionApiPersistenceMapper {

    public FunctionApi toDomain(FunctionApiJpaEntity e) {
        return new FunctionApi(
                e.getId().getFunctionId(),
                e.getId().getApiEndpointId(),
                e.getNote(),
                e.getCreatedAt());
    }

    public FunctionApiJpaEntity toJpaEntity(FunctionApi d) {
        FunctionApiJpaEntity e = new FunctionApiJpaEntity();
        e.setId(new FunctionApiId(d.functionId(), d.apiEndpointId()));
        e.setNote(d.note());
        e.setCreatedAt(d.createdAt());
        e.setCreatedBy("SYSTEM");
        return e;
    }
}
