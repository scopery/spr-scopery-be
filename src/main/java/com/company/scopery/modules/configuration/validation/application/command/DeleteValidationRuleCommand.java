package com.company.scopery.modules.configuration.validation.application.command;
import java.util.UUID;
public record DeleteValidationRuleCommand(UUID workspaceId, UUID ruleId) {}
