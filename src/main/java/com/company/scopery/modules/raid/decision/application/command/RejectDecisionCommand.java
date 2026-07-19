package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record RejectDecisionCommand(UUID projectId, UUID decisionId, String reason) {}
