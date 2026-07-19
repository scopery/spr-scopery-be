package com.company.scopery.modules.reporting.exportjob.application.command;

import java.util.UUID;

public record CreateReportExportCommand(
        UUID reportRunId,
        String format,
        String fileName
) {}
