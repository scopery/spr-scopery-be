package com.company.scopery.modules.resourcereference.resourcetype.application.command;

public record CreateResourceTypeCommand(
        String code,
        String displayName,
        String description,
        boolean isSystem
) {}
