package com.company.scopery.modules.raid.decisionlink.application.command;

import java.util.UUID;

public record DeleteDecisionLinkCommand(UUID projectId, UUID decisionId, UUID linkId) {}
