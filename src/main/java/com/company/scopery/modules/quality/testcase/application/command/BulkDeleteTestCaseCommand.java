package com.company.scopery.modules.quality.testcase.application.command;

import java.util.List;
import java.util.UUID;

public record BulkDeleteTestCaseCommand(UUID projectId, List<UUID> ids) {}
