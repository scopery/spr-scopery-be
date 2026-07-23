package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.dataentity.domain.model.RegistryDataEntity;

import java.util.UUID;

public record EntityRef(UUID id, String code, String name, String tableName) {
    public static EntityRef from(RegistryDataEntity e) {
        return new EntityRef(e.id(), e.code(), e.name(), e.tableName());
    }
}
