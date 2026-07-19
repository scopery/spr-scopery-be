package com.company.scopery.modules.reporting.exportjob.application.response;

import java.util.UUID;

public record CreateReportExportResponse(
        UUID exportJobId,
        String status,
        String format,
        String fileName,
        boolean downloadAvailable
) {}
