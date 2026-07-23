package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;

import java.util.List;
import java.util.UUID;

public record ScreenRef(UUID id, String code, String name, String routePath, List<ComponentRef> components) {
    public static ScreenRef from(RegistryScreen s, List<ComponentRef> components) {
        return new ScreenRef(s.id(), s.code(), s.name(), s.routePath(), components);
    }
}
