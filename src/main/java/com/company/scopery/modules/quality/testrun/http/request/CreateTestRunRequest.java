package com.company.scopery.modules.quality.testrun.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List; import java.util.UUID;
public record CreateTestRunRequest(@NotBlank String name,
        @NotBlank @Schema(allowableValues={"MANUAL","AUTOMATED","REGRESSION","SMOKE"}) String runType,
        @Schema(allowableValues={"FUNCTIONAL","NON_FUNCTIONAL","MIXED"}) String runScope,
        UUID testPlanId, UUID testSuiteId, UUID releasePackageId,
        List<CaseIdRequest> caseIds) {
    public record CaseIdRequest(@Schema(allowableValues={"FUNCTIONAL","NFR"}) String caseKind, UUID caseId) {}
}
