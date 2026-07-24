package com.company.scopery.modules.aiassistant.guide.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.guide.application.action.CreateGuideDefinitionAction;
import com.company.scopery.modules.aiassistant.guide.application.action.RetireGuideDefinitionAction;
import com.company.scopery.modules.aiassistant.guide.application.action.UpdateGuideDefinitionAction;
import com.company.scopery.modules.aiassistant.guide.application.command.CreateGuideDefinitionCommand;
import com.company.scopery.modules.aiassistant.guide.application.command.UpdateGuideDefinitionCommand;
import com.company.scopery.modules.aiassistant.guide.application.response.AdminGuideDefinitionResponse;
import com.company.scopery.modules.aiassistant.guide.application.service.AiGuideQueryService;
import com.company.scopery.modules.aiassistant.guide.http.request.CreateGuideDefinitionRequest;
import com.company.scopery.modules.aiassistant.guide.http.request.UpdateGuideDefinitionRequest;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin - AI Guide Definitions")
@RestController
@RequestMapping(AiAssistantApiPaths.ADMIN_GUIDES)
public class AdminGuideDefinitionController {

    private final AiGuideQueryService queryService;
    private final CreateGuideDefinitionAction createAction;
    private final UpdateGuideDefinitionAction updateAction;
    private final RetireGuideDefinitionAction retireAction;

    public AdminGuideDefinitionController(AiGuideQueryService queryService,
                                           CreateGuideDefinitionAction createAction,
                                           UpdateGuideDefinitionAction updateAction,
                                           RetireGuideDefinitionAction retireAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.retireAction = retireAction;
    }

    @Operation(summary = "List all guide definitions")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminGuideDefinitionResponse>>> listAll() {
        List<AdminGuideDefinitionResponse> response = queryService.findAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get a guide definition by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminGuideDefinitionResponse>> getById(@PathVariable UUID id) {
        AdminGuideDefinitionResponse response = queryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Create a guide definition")
    @PostMapping
    public ResponseEntity<ApiResponse<AdminGuideDefinitionResponse>> create(
            @Valid @RequestBody CreateGuideDefinitionRequest request) {

        CreateGuideDefinitionCommand command = new CreateGuideDefinitionCommand(
                request.pageCode(),
                request.locale(),
                request.title(),
                request.bodyMarkdown(),
                request.fieldCode(),
                request.actionCode()
        );
        AdminGuideDefinitionResponse response = createAction.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "Update a guide definition")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminGuideDefinitionResponse>> update(
            @PathVariable UUID id,
            @RequestBody UpdateGuideDefinitionRequest request) {

        UpdateGuideDefinitionCommand command = new UpdateGuideDefinitionCommand(
                id,
                request.title(),
                request.bodyMarkdown(),
                request.status()
        );
        AdminGuideDefinitionResponse response = updateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Retire a guide definition")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retire(@PathVariable UUID id) {
        retireAction.execute(id);
        return ResponseEntity.noContent().build();
    }
}
