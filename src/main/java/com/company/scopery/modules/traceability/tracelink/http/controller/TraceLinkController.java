package com.company.scopery.modules.traceability.tracelink.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.tracelink.application.action.*;
import com.company.scopery.modules.traceability.tracelink.application.command.CreateTraceLinkCommand;
import com.company.scopery.modules.traceability.tracelink.application.response.TraceLinkResponse;
import com.company.scopery.modules.traceability.tracelink.application.service.TraceLinkQueryService;
import com.company.scopery.modules.traceability.tracelink.http.request.CreateTraceLinkRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.TRACE_LINKS)
@Tag(name = "Traceability - Trace Links")
public class TraceLinkController {
    private final CreateTraceLinkAction create;
    private final ArchiveTraceLinkAction archive;
    private final TraceLinkQueryService query;
    public TraceLinkController(CreateTraceLinkAction create, ArchiveTraceLinkAction archive, TraceLinkQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Create trace link")
    public ApiResponse<TraceLinkResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateTraceLinkRequest r) {
        return ApiResponse.success(create.execute(new CreateTraceLinkCommand(projectId, r.sourceType(), r.sourceId(), r.targetType(), r.targetId(), r.linkType())));
    }
    @GetMapping @Operation(summary = "List trace links")
    public ApiResponse<List<TraceLinkResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{linkId}") @Operation(summary = "Get trace link")
    public ApiResponse<TraceLinkResponse> get(@PathVariable UUID projectId, @PathVariable UUID linkId) {
        return ApiResponse.success(query.get(projectId, linkId));
    }
    @PatchMapping("/{linkId}/archive") @Operation(summary = "Archive trace link")
    public ApiResponse<TraceLinkResponse> archive(@PathVariable UUID projectId, @PathVariable UUID linkId) {
        return ApiResponse.success(archive.execute(projectId, linkId));
    }
}
