package com.company.scopery.modules.resourcecapacity.taskassignment.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import com.company.scopery.modules.resourcecapacity.taskassignment.application.action.*;
import com.company.scopery.modules.resourcecapacity.taskassignment.application.response.TaskResourceAssignmentResponse;
import com.company.scopery.modules.resourcecapacity.taskassignment.application.service.TaskResourceAssignmentQueryService;
import com.company.scopery.modules.resourcecapacity.taskassignment.http.request.CreateTaskResourceAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Resource Capacity - Task Assignments")
public class TaskResourceAssignmentController {
    private final CreateTaskResourceAssignmentAction create; private final RemoveTaskResourceAssignmentAction remove;
    private final TaskResourceAssignmentQueryService query;
    public TaskResourceAssignmentController(CreateTaskResourceAssignmentAction create, RemoveTaskResourceAssignmentAction remove,
                                            TaskResourceAssignmentQueryService query) {
        this.create=create; this.remove=remove; this.query=query;
    }
    @PostMapping(CapacityApiPaths.TASK_ASSIGNMENTS) @Operation(summary="Assign resource to task")
    public ApiResponse<TaskResourceAssignmentResponse> create(@PathVariable UUID projectId, @PathVariable UUID taskId,
            @Valid @RequestBody CreateTaskResourceAssignmentRequest r) {
        return ApiResponse.success(create.execute(projectId, taskId, r));
    }
    @GetMapping(CapacityApiPaths.TASK_ASSIGNMENTS) @Operation(summary="List task resource assignments")
    public ApiResponse<List<TaskResourceAssignmentResponse>> list(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        return ApiResponse.success(query.listByTask(projectId, taskId));
    }
    @DeleteMapping(CapacityApiPaths.TASK_ASSIGNMENTS + "/{assignmentId}") @Operation(summary="Remove task resource assignment")
    public ApiResponse<TaskResourceAssignmentResponse> remove(@PathVariable UUID projectId, @PathVariable UUID taskId, @PathVariable UUID assignmentId) {
        return ApiResponse.success(remove.execute(projectId, taskId, assignmentId, null));
    }
}
