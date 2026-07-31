package com.company.scopery.modules.quality.testrun.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
public record UpdateTestCaseResultRequest(
        @NotBlank @Schema(allowableValues={"NOT_RUN","PASSED","FAILED","BLOCKED","SKIPPED"}) String result,
        String comment, Long version) {}
