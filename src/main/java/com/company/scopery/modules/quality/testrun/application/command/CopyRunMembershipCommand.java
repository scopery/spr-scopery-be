package com.company.scopery.modules.quality.testrun.application.command;
import java.util.UUID;
public record CopyRunMembershipCommand(UUID projectId, UUID testRunId, UUID sourceRunId, boolean replaceExisting) {}
