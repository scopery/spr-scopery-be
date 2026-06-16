package com.company.scopery.modules.iam.resource.application.command;

import java.util.UUID;

public record UpdateIamAuthResourceCommand(UUID id, String name, String description) {}
