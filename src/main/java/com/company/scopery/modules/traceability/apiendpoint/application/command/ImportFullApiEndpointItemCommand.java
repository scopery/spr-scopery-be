package com.company.scopery.modules.traceability.apiendpoint.application.command;

import java.util.List;
import java.util.UUID;

public record ImportFullApiEndpointItemCommand(
        UUID applicationId,
        UUID workspaceId,
        UUID projectId,
        String method,
        String pathPattern,
        String name,
        String description,
        List<ParamItem> requestParams,
        String responseSchemaJson) {

    public record ParamItem(
            String name,
            String in,
            String type,
            Boolean required,
            String description,
            String example) {}
}
