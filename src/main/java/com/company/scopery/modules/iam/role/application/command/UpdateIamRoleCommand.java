package com.company.scopery.modules.iam.role.application.command;

import java.util.UUID;

public record UpdateIamRoleCommand(UUID id, String name, String description) {}
