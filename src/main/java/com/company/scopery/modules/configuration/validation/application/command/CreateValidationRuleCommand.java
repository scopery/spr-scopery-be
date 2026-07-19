package com.company.scopery.modules.configuration.validation.application.command;
import java.util.UUID;
public record CreateValidationRuleCommand(UUID workspaceId, UUID fieldId, String ruleType, String config) {}
