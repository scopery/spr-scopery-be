package com.company.scopery.modules.project.scheduling.schedulerun.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.application.action.*;
import com.company.scopery.modules.project.scheduling.schedulerun.application.command.CreateScheduleRunCommand;
import com.company.scopery.modules.project.scheduling.schedulerun.application.response.ScheduleRunResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.application.service.ScheduleQueryService;
import com.company.scopery.modules.project.scheduling.schedulerun.http.request.CreateScheduleRunRequest;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping(ProjectApiPaths.SCHEDULE_RUNS) @Tag(name="Project - Schedule Runs")
public class ScheduleRunController {
    private final CreateScheduleRunAction create;private final CancelScheduleRunAction cancel;private final ScheduleQueryService query;
    public ScheduleRunController(CreateScheduleRunAction create,CancelScheduleRunAction cancel,ScheduleQueryService query){this.create=create;this.cancel=cancel;this.query=query;}
    @PostMapping @Operation(summary="Create and execute a schedule run")
    public ApiResponse<ScheduleRunResponse> create(@PathVariable UUID projectId,@Valid @RequestBody CreateScheduleRunRequest request){
        return ApiResponse.success(create.execute(new CreateScheduleRunCommand(projectId,request.planningStartDate(),request.planningEndDate(),request.includeCompletedTasks(),request.markAsCurrent())));
    }
    @GetMapping @Operation(summary="List schedule runs")
    public ApiResponse<List<ScheduleRunResponse>> list(@PathVariable UUID projectId){return ApiResponse.success(query.listRuns(projectId));}
    @GetMapping("/{scheduleRunId}") @Operation(summary="Get schedule run")
    public ApiResponse<ScheduleRunResponse> get(@PathVariable UUID projectId,@PathVariable UUID scheduleRunId){return ApiResponse.success(query.getRun(projectId,scheduleRunId));}
    @PostMapping("/{scheduleRunId}/cancel") @Operation(summary="Cancel pending or running schedule run")
    public ApiResponse<ScheduleRunResponse> cancel(@PathVariable UUID projectId,@PathVariable UUID scheduleRunId){return ApiResponse.success(cancel.execute(projectId,scheduleRunId));}
}
