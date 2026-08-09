package com.company.scopery.modules.project.wbs.application.command;

import java.util.UUID;

public record DeleteWbsNodeCommand(UUID id, UUID projectId) {}
