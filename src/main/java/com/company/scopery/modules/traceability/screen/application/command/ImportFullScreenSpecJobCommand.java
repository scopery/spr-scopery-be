package com.company.scopery.modules.traceability.screen.application.command;

import java.util.List;

public record ImportFullScreenSpecJobCommand(List<ImportFullScreenSpecItemCommand> items) {}
