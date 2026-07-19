package com.company.scopery.modules.documenthub.template.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.nativecontent.application.response.DocumentContentResponse;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import com.company.scopery.modules.documenthub.template.application.action.CreateDocumentTemplateAction;
import com.company.scopery.modules.documenthub.template.application.action.InstantiateNativeTemplateAction;
import com.company.scopery.modules.documenthub.template.application.action.PublishNativeTemplateVersionAction;
import com.company.scopery.modules.documenthub.template.application.command.CreateDocumentTemplateCommand;
import com.company.scopery.modules.documenthub.template.application.command.InstantiateNativeTemplateCommand;
import com.company.scopery.modules.documenthub.template.application.command.PublishNativeTemplateVersionCommand;
import com.company.scopery.modules.documenthub.template.application.response.DocumentTemplateResponse;
import com.company.scopery.modules.documenthub.template.application.response.NativeTemplateVersionResponse;
import com.company.scopery.modules.documenthub.template.application.service.DocumentTemplateQueryService;
import com.company.scopery.modules.documenthub.template.http.request.CreateDocumentTemplateRequest;
import com.company.scopery.modules.documenthub.template.http.request.InstantiateNativeTemplateRequest;
import com.company.scopery.modules.documenthub.template.http.request.PublishNativeTemplateVersionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(DocumentHubApiPaths.TEMPLATES)
@Tag(name = "Document Hub - Templates")
public class DocumentTemplateController {
    private final CreateDocumentTemplateAction create;
    private final DocumentTemplateQueryService query;
    private final PublishNativeTemplateVersionAction publishNativeVersion;
    private final InstantiateNativeTemplateAction instantiateNativeTemplate;

    public DocumentTemplateController(CreateDocumentTemplateAction create,
                                      DocumentTemplateQueryService query,
                                      PublishNativeTemplateVersionAction publishNativeVersion,
                                      InstantiateNativeTemplateAction instantiateNativeTemplate) {
        this.create = create;
        this.query = query;
        this.publishNativeVersion = publishNativeVersion;
        this.instantiateNativeTemplate = instantiateNativeTemplate;
    }

    @PostMapping
    @Operation(summary = "Create template")
    public ApiResponse<DocumentTemplateResponse> create(@PathVariable UUID workspaceId,
                                                        @Valid @RequestBody CreateDocumentTemplateRequest r) {
        return ApiResponse.success(create.execute(new CreateDocumentTemplateCommand(
                workspaceId, r.code(), r.name(), r.description(), r.category())));
    }

    @GetMapping
    @Operation(summary = "List templates")
    public ApiResponse<List<DocumentTemplateResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.list(workspaceId));
    }

    @GetMapping("/{templateId}")
    @Operation(summary = "Get template")
    public ApiResponse<DocumentTemplateResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID templateId) {
        return ApiResponse.success(query.get(workspaceId, templateId));
    }

    @PostMapping("/{templateId}/native-versions")
    @Operation(summary = "Publish native template version")
    public ApiResponse<NativeTemplateVersionResponse> publishNativeVersion(@PathVariable UUID workspaceId,
                                                                           @PathVariable UUID templateId,
                                                                           @Valid @RequestBody PublishNativeTemplateVersionRequest r) {
        List<PublishNativeTemplateVersionCommand.VariableDefinition> variables = r.variables() == null ? null
                : r.variables().stream()
                .map(v -> new PublishNativeTemplateVersionCommand.VariableDefinition(
                        v.variableKey(), v.label(), v.variableType(), v.required(),
                        v.defaultValue(), v.sensitive(), v.ordinal()))
                .toList();
        return ApiResponse.success(publishNativeVersion.execute(new PublishNativeTemplateVersionCommand(
                workspaceId, templateId, r.ast(), variables)));
    }

    @PostMapping("/{templateId}/native-versions/{versionId}/instantiate")
    @Operation(summary = "Instantiate native template into document")
    public ApiResponse<DocumentContentResponse> instantiateNativeTemplate(@PathVariable UUID workspaceId,
                                                                          @PathVariable UUID templateId,
                                                                          @PathVariable UUID versionId,
                                                                          @Valid @RequestBody InstantiateNativeTemplateRequest r) {
        return ApiResponse.success(instantiateNativeTemplate.execute(new InstantiateNativeTemplateCommand(
                workspaceId, templateId, versionId, r.projectId(), r.targetDocumentId(), r.variables())));
    }
}
