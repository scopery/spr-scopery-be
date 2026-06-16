package com.company.scopery.modules.iam.user.application.command;

import java.util.UUID;

public record UpdateIamUserCommand(UUID id, String fullName) {}
