package com.company.scopery.modules.traceability.functioncomm.infrastructure.mapper;

import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunication;
import com.company.scopery.modules.traceability.functioncomm.infrastructure.persistence.FunctionCommunicationId;
import com.company.scopery.modules.traceability.functioncomm.infrastructure.persistence.FunctionCommunicationJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FunctionCommunicationPersistenceMapper {

    public FunctionCommunication toDomain(FunctionCommunicationJpaEntity e) {
        return new FunctionCommunication(
                e.getId().getFunctionId(),
                e.getId().getCommunicationId(),
                e.getNote(),
                e.getCreatedAt());
    }

    public FunctionCommunicationJpaEntity toJpaEntity(FunctionCommunication d) {
        FunctionCommunicationJpaEntity e = new FunctionCommunicationJpaEntity();
        e.setId(new FunctionCommunicationId(d.functionId(), d.communicationId()));
        e.setNote(d.note());
        e.setCreatedAt(d.createdAt());
        e.setCreatedBy("SYSTEM");
        return e;
    }
}
