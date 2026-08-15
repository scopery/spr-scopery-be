package com.company.scopery.modules.traceability.apiendpoint.application.command;

import java.util.List;

public record ImportFullApiEndpointJobCommand(List<ImportFullApiEndpointItemCommand> items) {}
