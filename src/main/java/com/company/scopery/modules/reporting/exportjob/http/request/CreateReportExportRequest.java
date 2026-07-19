package com.company.scopery.modules.reporting.exportjob.http.request;

public record CreateReportExportRequest(
        String format,
        String fileName
) {}
