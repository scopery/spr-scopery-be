package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;

import java.util.List;
import java.util.UUID;

public record FunctionStructure(UUID id, UUID projectId, String code, String title, String type,
                                 List<ScreenRef> screens, List<ApiRef> apis,
                                 List<CommRef> communications) {
    public static FunctionStructure from(
            FunctionalItem fi,
            List<ScreenRef> screens,
            List<ApiRef> apis,
            List<CommRef> communications) {
        return new FunctionStructure(fi.id(), fi.projectId(), fi.code(), fi.title(),
                fi.type() != null ? fi.type().name() : null, screens, apis, communications);
    }
}
