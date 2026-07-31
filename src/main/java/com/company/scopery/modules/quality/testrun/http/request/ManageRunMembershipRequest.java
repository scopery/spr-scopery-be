package com.company.scopery.modules.quality.testrun.http.request;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List; import java.util.UUID;
public record ManageRunMembershipRequest(List<CaseRefRequest> add, List<CaseRefRequest> remove) {
    public record CaseRefRequest(
            @NotNull @Schema(allowableValues={"FUNCTIONAL","NFR"}) String caseKind,
            @NotNull UUID caseId) {}
}
