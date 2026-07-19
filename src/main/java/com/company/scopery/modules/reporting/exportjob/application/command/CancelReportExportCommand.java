package com.company.scopery.modules.reporting.exportjob.application.command;

import java.util.UUID;

public record CancelReportExportCommand(UUID exportJobId) {}
