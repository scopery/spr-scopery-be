package com.company.scopery.modules.configuration.taxonomy.application.command;
import java.util.UUID;
public record CreateTaxonomyTermCommand(UUID workspaceId, UUID taxonomyId, String code, String label, UUID parentId) {}
