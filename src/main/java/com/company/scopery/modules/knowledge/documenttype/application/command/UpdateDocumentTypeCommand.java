package com.company.scopery.modules.knowledge.documenttype.application.command;

import java.util.UUID;

public record UpdateDocumentTypeCommand(UUID id, String name, String description) {}
