package com.company.scopery.modules.quality.testrun.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CopyRunMembershipRequest(@NotNull UUID sourceRunId, boolean replaceExisting) {}
