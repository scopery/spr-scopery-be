package com.company.scopery.modules.project.scheduling.taskschedule.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.project.scheduling.schedulerun.application.service.ScheduleQueryService;
import com.company.scopery.modules.project.scheduling.taskschedule.application.response.TaskScheduleResponse;
import com.company.scopery.modules.project.shared.constant.ProjectApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping(ProjectApiPaths.TASK_SCHEDULE) @Tag(name="Project - Task Schedule")
public class TaskScheduleController {
    private final ScheduleQueryService query;
    public TaskScheduleController(ScheduleQueryService query){this.query=query;}
    @GetMapping @Operation(summary="Get task schedule in current run")
    public ApiResponse<TaskScheduleResponse> current(@PathVariable UUID projectId,@PathVariable UUID taskId){return ApiResponse.success(query.taskCurrent(projectId,taskId));}
    @GetMapping("/history") @Operation(summary="Get task schedule history")
    public ApiResponse<List<TaskScheduleResponse>> history(@PathVariable UUID projectId,@PathVariable UUID taskId){return ApiResponse.success(query.taskHistory(projectId,taskId));}
}
