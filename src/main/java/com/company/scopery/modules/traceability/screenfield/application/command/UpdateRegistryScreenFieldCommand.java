package com.company.scopery.modules.traceability.screenfield.application.command;
import java.util.UUID;
public record UpdateRegistryScreenFieldCommand(UUID workspaceId, UUID fieldId, String label, String fieldType, String description, boolean required, int displayOrder) {}
