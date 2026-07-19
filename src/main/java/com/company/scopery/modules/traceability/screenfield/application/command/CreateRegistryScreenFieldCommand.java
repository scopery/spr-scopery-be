package com.company.scopery.modules.traceability.screenfield.application.command;
import java.util.UUID;
public record CreateRegistryScreenFieldCommand(UUID screenId, UUID sectionId, UUID workspaceId, String fieldKey,
                                               String label, String fieldType, String description,
                                               boolean required, int displayOrder) {}
