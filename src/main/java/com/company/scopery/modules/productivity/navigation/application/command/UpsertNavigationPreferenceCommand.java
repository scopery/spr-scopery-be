package com.company.scopery.modules.productivity.navigation.application.command;
import java.util.UUID;
public record UpsertNavigationPreferenceCommand(UUID workspaceId, String preferenceJson, String landing) {}
