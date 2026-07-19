package com.company.scopery.modules.aicontext.resolution.application.command;

import java.util.UUID;

public record ResolveAiContextCommand(UUID policyId, UUID projectId, UUID documentId) {}
