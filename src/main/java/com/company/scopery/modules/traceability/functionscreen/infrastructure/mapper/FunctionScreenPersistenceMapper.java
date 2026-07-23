package com.company.scopery.modules.traceability.functionscreen.infrastructure.mapper;

import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;
import com.company.scopery.modules.traceability.functionscreen.infrastructure.persistence.FunctionScreenId;
import com.company.scopery.modules.traceability.functionscreen.infrastructure.persistence.FunctionScreenJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class FunctionScreenPersistenceMapper {

    public FunctionScreen toDomain(FunctionScreenJpaEntity e) {
        return new FunctionScreen(
                e.getId().getFunctionId(),
                e.getId().getScreenId(),
                e.getNote(),
                e.getCreatedAt());
    }

    public FunctionScreenJpaEntity toJpaEntity(FunctionScreen d) {
        FunctionScreenJpaEntity e = new FunctionScreenJpaEntity();
        e.setId(new FunctionScreenId(d.functionId(), d.screenId()));
        e.setNote(d.note());
        e.setCreatedAt(d.createdAt());
        e.setCreatedBy("SYSTEM");
        return e;
    }
}
