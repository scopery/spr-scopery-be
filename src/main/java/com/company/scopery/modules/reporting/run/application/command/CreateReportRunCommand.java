package com.company.scopery.modules.reporting.run.application.command;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateReportRunCommand(
        String reportCode,
        UUID projectId,
        Map<String, Object> filters,
        List<String> selectedFields
) {
    public CreateReportRunCommand {
        filters = filters == null ? Map.of() : filters;
        selectedFields = selectedFields == null ? List.of() : selectedFields;
    }
}
