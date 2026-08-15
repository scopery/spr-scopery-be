package com.company.scopery.modules.traceability.appcomponent.application.command;

import java.util.List;

public record ImportFullAppComponentJobCommand(List<ImportFullAppComponentItemCommand> items) {}
