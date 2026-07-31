package com.company.scopery.modules.quality.testrun.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*; import java.util.UUID;
public record RecordCaseResultRequest(@NotNull UUID testCaseId,
        @NotBlank @Schema(allowableValues={"NOT_RUN","PASSED","FAILED","BLOCKED","SKIPPED"}) String resultStatus,
        String actualResult) {}
