package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;

import java.util.UUID;

public record ComponentRef(UUID id, String code, String name, String componentType, int displayOrder) {
    public static ComponentRef from(RegistryAppComponent c, int displayOrder) {
        return new ComponentRef(c.id(), c.code(), c.name(), c.componentType(), displayOrder);
    }
}
