package com.company.scopery.modules.quality.testrun.http.request;
import jakarta.validation.constraints.*; import java.util.UUID;
public record RecordCaseResultRequest(@NotNull UUID testCaseId, @NotBlank String resultStatus, String actualResult) {}
