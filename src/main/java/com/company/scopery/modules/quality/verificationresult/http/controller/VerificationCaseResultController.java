package com.company.scopery.modules.quality.verificationresult.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import com.company.scopery.modules.quality.verificationresult.application.action.*;
import com.company.scopery.modules.quality.verificationresult.application.command.*;
import com.company.scopery.modules.quality.verificationresult.application.response.VerificationCaseResultResponse;
import com.company.scopery.modules.quality.verificationresult.application.service.VerificationCaseResultQueryService;
import com.company.scopery.modules.quality.verificationresult.http.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(QualityApiPaths.VERIFICATION_RESULTS) @Tag(name="Quality - Verification Results")
public class VerificationCaseResultController {
    private final RecordVerificationResultAction record;
    private final UpdateVerificationResultAction update;
    private final VerificationCaseResultQueryService query;
    public VerificationCaseResultController(RecordVerificationResultAction record, UpdateVerificationResultAction update, VerificationCaseResultQueryService query) {
        this.record=record; this.update=update; this.query=query;
    }
    @PostMapping @Operation(summary="Record or upsert verification result")
    public ApiResponse<VerificationCaseResultResponse> record(@PathVariable UUID projectId, @PathVariable UUID testRunId, @Valid @RequestBody RecordVerificationResultRequest r) {
        return ApiResponse.success(record.execute(new RecordVerificationResultCommand(projectId, testRunId, r.verificationCaseId(), r.resultStatus(), r.actualValue(), r.actualValueUnit(), r.actualResultJson(), r.evidenceReference(), r.executedById(), r.defectId(), r.comment())));
    }
    @GetMapping @Operation(summary="List verification results for test run")
    public ApiResponse<List<VerificationCaseResultResponse>> list(@PathVariable UUID projectId, @PathVariable UUID testRunId) {
        return ApiResponse.success(query.listByTestRun(projectId, testRunId));
    }
    @GetMapping("/{resultId}") @Operation(summary="Get verification result")
    public ApiResponse<VerificationCaseResultResponse> get(@PathVariable UUID projectId, @PathVariable UUID testRunId, @PathVariable UUID resultId) {
        return ApiResponse.success(query.getById(projectId, resultId));
    }
    @PatchMapping("/{resultId}") @Operation(summary="Update verification result")
    public ApiResponse<VerificationCaseResultResponse> update(@PathVariable UUID projectId, @PathVariable UUID testRunId, @PathVariable UUID resultId, @RequestBody UpdateVerificationResultRequest r) {
        return ApiResponse.success(update.execute(new UpdateVerificationResultCommand(projectId, testRunId, resultId, r.resultStatus(), r.actualValue(), r.actualValueUnit(), r.actualResultJson(), r.evidenceReference(), r.executedById(), r.defectId(), r.comment(), r.version())));
    }
}
