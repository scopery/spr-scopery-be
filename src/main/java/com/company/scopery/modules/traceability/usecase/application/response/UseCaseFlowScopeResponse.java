package com.company.scopery.modules.traceability.usecase.application.response;

import java.util.List;
import java.util.UUID;

public record UseCaseFlowScopeResponse(
        UUID useCaseId,
        FunctionRef function,
        List<ScreenRef> screens,
        List<SimpleRef> apis,
        List<SimpleRef> entities
) {
    public record FunctionRef(UUID id, String code, String name) {}

    public record ScreenRef(UUID id, String code, String name, long componentCount) {}

    public record SimpleRef(UUID id, String name) {}
}
