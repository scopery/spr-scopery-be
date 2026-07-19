package com.company.scopery.modules.documenthub.template.application.command;
import java.util.UUID;
public record CreateDocumentTemplateCommand(UUID workspaceId, String code, String name, String description, String category) {}
