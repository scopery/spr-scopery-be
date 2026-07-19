package com.company.scopery.modules.configuration.taxonomy.application.command;
import java.util.UUID;
public record CreateTaxonomyCommand(UUID workspaceId, String code, String name) {}
