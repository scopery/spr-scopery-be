package com.company.scopery.modules.productivity.savedsearch.application.command;
import java.util.UUID;
public record CreateSavedSearchCommand(UUID workspaceId, String name, String scope, String queryText, String filtersJson, UUID projectId) {}
