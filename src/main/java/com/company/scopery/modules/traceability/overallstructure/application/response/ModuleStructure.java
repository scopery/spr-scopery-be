package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;

import java.util.List;
import java.util.UUID;

public record ModuleStructure(UUID id, String code, String name,
                               List<FunctionStructure> functions, List<EntityRef> entities) {
    public static ModuleStructure from(RegistryAppModule m, List<FunctionStructure> functions, List<EntityRef> entities) {
        return new ModuleStructure(m.id(), m.code(), m.name(), functions, entities);
    }
}
