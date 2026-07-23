package com.company.scopery.modules.traceability.screensection.application.command;
import java.util.UUID;
public record UpdateRegistryScreenSectionCommand(UUID workspaceId, UUID sectionId, String name, String description, int displayOrder) {}
