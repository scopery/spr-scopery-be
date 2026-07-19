package com.company.scopery.modules.scope.mapping.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.mapping.application.action.ArchiveDeliverableTaskMappingAction;
import com.company.scopery.modules.scope.mapping.application.action.ArchiveScopeItemWbsMappingAction;
import com.company.scopery.modules.scope.mapping.application.action.CreateDeliverableTaskMappingAction;
import com.company.scopery.modules.scope.mapping.application.action.CreateScopeItemWbsMappingAction;
import com.company.scopery.modules.scope.mapping.application.command.ArchiveDeliverableTaskMappingCommand;
import com.company.scopery.modules.scope.mapping.application.command.ArchiveScopeItemWbsMappingCommand;
import com.company.scopery.modules.scope.mapping.application.command.CreateDeliverableTaskMappingCommand;
import com.company.scopery.modules.scope.mapping.application.command.CreateScopeItemWbsMappingCommand;
import com.company.scopery.modules.scope.mapping.application.response.DeliverableTaskMappingResponse;
import com.company.scopery.modules.scope.mapping.application.response.ScopeItemWbsMappingResponse;
import com.company.scopery.modules.scope.mapping.application.service.ScopeMappingQueryService;
import com.company.scopery.modules.scope.mapping.http.request.CreateTaskMappingRequest;
import com.company.scopery.modules.scope.mapping.http.request.CreateWbsMappingRequest;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@Tag(name = "Scope - Mappings")
public class MappingController {
    private final CreateScopeItemWbsMappingAction createWbs;
    private final ArchiveScopeItemWbsMappingAction archiveWbs;
    private final CreateDeliverableTaskMappingAction createTask;
    private final ArchiveDeliverableTaskMappingAction archiveTask;
    private final ScopeMappingQueryService query;
    public MappingController(CreateScopeItemWbsMappingAction createWbs, ArchiveScopeItemWbsMappingAction archiveWbs,
                             CreateDeliverableTaskMappingAction createTask, ArchiveDeliverableTaskMappingAction archiveTask,
                             ScopeMappingQueryService query) {
        this.createWbs = createWbs; this.archiveWbs = archiveWbs; this.createTask = createTask;
        this.archiveTask = archiveTask; this.query = query;
    }
    @PostMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}/wbs-mappings")
    @Operation(summary = "Create scope item WBS mapping")
    public ApiResponse<ScopeItemWbsMappingResponse> createWbs(@PathVariable UUID projectId, @PathVariable UUID scopeItemId,
                                                            @Valid @RequestBody CreateWbsMappingRequest request) {
        return ApiResponse.success(createWbs.execute(new CreateScopeItemWbsMappingCommand(
                projectId, scopeItemId, request.wbsNodeId(), request.mappingType())));
    }
    @GetMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}/wbs-mappings")
    @Operation(summary = "List active scope item WBS mappings")
    public ApiResponse<List<ScopeItemWbsMappingResponse>> listWbs(@PathVariable UUID projectId,
                                                                   @PathVariable UUID scopeItemId) {
        return ApiResponse.success(query.listWbsMappings(projectId, scopeItemId));
    }
    @DeleteMapping(ScopeApiPaths.ITEMS + "/wbs-mappings/{mappingId}")
    @Operation(summary = "Archive scope item WBS mapping")
    public ApiResponse<ScopeItemWbsMappingResponse> archiveWbs(@PathVariable UUID projectId, @PathVariable UUID mappingId) {
        return ApiResponse.success(archiveWbs.execute(new ArchiveScopeItemWbsMappingCommand(projectId, mappingId)));
    }
    @PostMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/task-mappings")
    @Operation(summary = "Create deliverable task mapping")
    public ApiResponse<DeliverableTaskMappingResponse> createTask(@PathVariable UUID projectId,
                                                                  @PathVariable UUID deliverableId,
                                                                  @Valid @RequestBody CreateTaskMappingRequest request) {
        return ApiResponse.success(createTask.execute(new CreateDeliverableTaskMappingCommand(
                projectId, deliverableId, request.taskId(), request.mappingType())));
    }
    @GetMapping(ScopeApiPaths.DELIVERABLES + "/{deliverableId}/task-mappings")
    @Operation(summary = "List active deliverable task mappings")
    public ApiResponse<List<DeliverableTaskMappingResponse>> listTask(@PathVariable UUID projectId,
                                                                      @PathVariable UUID deliverableId) {
        return ApiResponse.success(query.listTaskMappings(projectId, deliverableId));
    }
    @DeleteMapping(ScopeApiPaths.DELIVERABLES + "/task-mappings/{mappingId}")
    @Operation(summary = "Archive deliverable task mapping")
    public ApiResponse<DeliverableTaskMappingResponse> archiveTask(@PathVariable UUID projectId, @PathVariable UUID mappingId) {
        return ApiResponse.success(archiveTask.execute(new ArchiveDeliverableTaskMappingCommand(projectId, mappingId)));
    }
}
