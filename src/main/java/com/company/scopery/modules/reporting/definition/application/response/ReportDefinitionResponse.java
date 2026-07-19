package com.company.scopery.modules.reporting.definition.application.response;

import java.util.UUID;

public record ReportDefinitionResponse(
        UUID id,
        String code,
        String name,
        String reportType,
        String scope,
        String status,
        String supportedFormatsJson
) {}
