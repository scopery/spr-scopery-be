package com.company.scopery.modules.traceability.usecase.application.response;

import java.util.UUID;

public record SupportingFunctionResponse(UUID functionId, String functionName, String functionCode) {}
