package com.company.scopery.modules.configuration.fieldvalue.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.fieldvalue.application.action.UpsertFieldValuesAction;
import com.company.scopery.modules.configuration.fieldvalue.application.command.UpsertFieldValuesCommand;
import com.company.scopery.modules.configuration.fieldvalue.application.response.CustomFieldValueResponse;
import com.company.scopery.modules.configuration.fieldvalue.application.service.FieldValueQueryService;
import com.company.scopery.modules.configuration.fieldvalue.http.request.UpsertFieldValuesRequest;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@RestController
@RequestMapping(ConfigurationApiPaths.PROJECT_CUSTOM_FIELDS)
@Tag(name = "Configuration - Project Custom Field Shortcuts")
public class ProjectCustomFieldShortcutController {
    private final ProjectRepository projects;
    private final UpsertFieldValuesAction upsert;
    private final FieldValueQueryService query;

    public ProjectCustomFieldShortcutController(ProjectRepository projects, UpsertFieldValuesAction upsert, FieldValueQueryService query) {
        this.projects = projects; this.upsert = upsert; this.query = query;
    }

    @GetMapping @Operation(summary = "List custom field values for PROJECT target = projectId")
    public ApiResponse<List<CustomFieldValueResponse>> list(@PathVariable UUID projectId) {
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return ApiResponse.success(query.list(project.workspaceId(), "PROJECT", projectId));
    }

    @PutMapping @Operation(summary = "Upsert custom field values for PROJECT target = projectId")
    public ApiResponse<List<CustomFieldValueResponse>> upsert(@PathVariable UUID projectId, @Valid @RequestBody UpsertFieldValuesRequest r) {
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return ApiResponse.success(upsert.execute(new UpsertFieldValuesCommand(project.workspaceId(), "PROJECT", projectId, r.values().stream().map(i -> new UpsertFieldValuesCommand.FieldValueEntry(i.fieldId(), i.valueText(), i.valueLongText(), i.valueNumber(), i.valueDecimal(), i.valueBoolean(), i.valueDate(), i.valueDatetime(), i.valueJson(), i.valueOptionIds())).toList())));
    }

    @GetMapping("/by-target") @Operation(summary = "List values for nested target under project workspace")
    public ApiResponse<List<CustomFieldValueResponse>> listByTarget(@PathVariable UUID projectId,
                                                                    @RequestParam String objectType,
                                                                    @RequestParam UUID targetId) {
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return ApiResponse.success(query.list(project.workspaceId(), objectType, targetId));
    }

    @PutMapping("/by-target") @Operation(summary = "Upsert values for nested target under project workspace")
    public ApiResponse<List<CustomFieldValueResponse>> upsertByTarget(@PathVariable UUID projectId,
                                                                      @Valid @RequestBody UpsertFieldValuesRequest r) {
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        return ApiResponse.success(upsert.execute(new UpsertFieldValuesCommand(project.workspaceId(), r.objectType(), r.targetId(), r.values().stream().map(i -> new UpsertFieldValuesCommand.FieldValueEntry(i.fieldId(), i.valueText(), i.valueLongText(), i.valueNumber(), i.valueDecimal(), i.valueBoolean(), i.valueDate(), i.valueDatetime(), i.valueJson(), i.valueOptionIds())).toList())));
    }
}
