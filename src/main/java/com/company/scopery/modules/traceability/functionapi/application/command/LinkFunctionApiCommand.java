package com.company.scopery.modules.traceability.functionapi.application.command;

import java.util.UUID;

public record LinkFunctionApiCommand(UUID projectId, UUID functionalItemId, UUID apiEndpointId, String note) {}
