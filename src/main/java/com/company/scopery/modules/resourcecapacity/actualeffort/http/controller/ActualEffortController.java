package com.company.scopery.modules.resourcecapacity.actualeffort.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.action.CancelActualEffortAction;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.action.RecordActualEffortAction;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.response.ActualEffortRecordResponse;
import com.company.scopery.modules.resourcecapacity.actualeffort.application.service.ActualEffortQueryService;
import com.company.scopery.modules.resourcecapacity.actualeffort.http.request.CreateActualEffortRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;
@RestController @Tag(name = "Resource Capacity - Actual Effort")
public class ActualEffortController {
    private final RecordActualEffortAction record; private final CancelActualEffortAction cancel;
    private final ActualEffortQueryService query;
    public ActualEffortController(RecordActualEffortAction record, CancelActualEffortAction cancel, ActualEffortQueryService query) {
        this.record=record; this.cancel=cancel; this.query=query;
    }
    @PostMapping(CapacityApiPaths.ACTUAL_EFFORT) @Operation(summary="Record actual/observed effort (not payroll)")
    public ApiResponse<ActualEffortRecordResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateActualEffortRequest r) {
        return ApiResponse.success(record.execute(projectId, r));
    }
    @GetMapping(CapacityApiPaths.ACTUAL_EFFORT) @Operation(summary="List actual effort records")
    public ApiResponse<List<ActualEffortRecordResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @PostMapping(CapacityApiPaths.ACTUAL_EFFORT + "/{recordId}/cancel") @Operation(summary="Cancel actual effort record")
    public ApiResponse<ActualEffortRecordResponse> cancel(@PathVariable UUID projectId, @PathVariable UUID recordId,
                                                          @RequestBody(required=false) Map<String,String> body) {
        String reason = body == null ? null : body.get("cancellationReason");
        return ApiResponse.success(cancel.execute(projectId, recordId, null, reason));
    }
}
