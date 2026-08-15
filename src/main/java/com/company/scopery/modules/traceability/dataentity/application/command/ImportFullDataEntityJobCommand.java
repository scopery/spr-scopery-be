package com.company.scopery.modules.traceability.dataentity.application.command;

import java.util.List;

public record ImportFullDataEntityJobCommand(List<ImportFullDataEntityItemCommand> items) {}
