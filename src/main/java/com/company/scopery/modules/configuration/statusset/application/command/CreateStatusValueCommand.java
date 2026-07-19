package com.company.scopery.modules.configuration.statusset.application.command;
import java.util.UUID;
public record CreateStatusValueCommand(UUID workspaceId, UUID setId, String code, String label, String domainCategory, Integer sortOrder) {}
