package com.company.scopery.modules.integrationhub.importtemplate.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.importtemplate.application.response.ImportTemplateResponse;
import com.company.scopery.modules.integrationhub.importtemplate.application.service.ImportTemplateQueryService;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Import Templates")
public class ImportTemplateController {
    private final ImportTemplateQueryService query;
    public ImportTemplateController(ImportTemplateQueryService query) { this.query = query; }
    @GetMapping(IntegrationApiPaths.IMPORT_TEMPLATES) @Operation(summary = "List import templates")
    public ApiResponse<List<ImportTemplateResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }
    @GetMapping(IntegrationApiPaths.IMPORT_TEMPLATE_BY_ID) @Operation(summary = "Get import template by id")
    public ApiResponse<ImportTemplateResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID templateId) {
        return ApiResponse.success(query.getById(workspaceId, templateId));
    }
}
