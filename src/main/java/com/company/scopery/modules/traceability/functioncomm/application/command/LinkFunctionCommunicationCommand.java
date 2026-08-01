package com.company.scopery.modules.traceability.functioncomm.application.command;

import java.util.UUID;

public record LinkFunctionCommunicationCommand(
        UUID projectId,
        UUID functionalItemId,
        UUID communicationId,
        String note
) {}
