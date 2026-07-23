package com.company.scopery.modules.traceability.functionscreen.application.command;

import java.util.UUID;

public record LinkFunctionScreenCommand(UUID projectId, UUID functionalItemId, UUID screenId, String note) {}
