package com.company.scopery.modules.raid.decision.application.command;

import java.util.UUID;

public record UpdateDecisionCommand(UUID projectId, UUID decisionId, String title, String rationale) {}
