package com.company.scopery.modules.configuration.taxonomy.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.configuration.shared.constant.ConfigurationApiPaths;
import com.company.scopery.modules.configuration.taxonomy.application.action.*;
import com.company.scopery.modules.configuration.taxonomy.application.command.*;
import com.company.scopery.modules.configuration.taxonomy.application.response.*;
import com.company.scopery.modules.configuration.taxonomy.application.service.TaxonomyQueryService;
import com.company.scopery.modules.configuration.taxonomy.http.request.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(ConfigurationApiPaths.TAXONOMIES) @Tag(name = "Configuration - Taxonomies")
public class TaxonomyController {
    private final CreateTaxonomyAction create; private final CreateTaxonomyTermAction createTerm; private final TaxonomyQueryService query;
    public TaxonomyController(CreateTaxonomyAction create, CreateTaxonomyTermAction createTerm, TaxonomyQueryService query) {
        this.create=create; this.createTerm=createTerm; this.query=query;
    }
    @PostMapping @Operation(summary="Create taxonomy")
    public ApiResponse<TaxonomyResponse> create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateTaxonomyRequest r) {
        return ApiResponse.success(create.execute(new CreateTaxonomyCommand(workspaceId, r.taxonomyCode(), r.name())));
    }
    @GetMapping @Operation(summary="List taxonomies")
    public ApiResponse<List<TaxonomyResponse>> list(@PathVariable UUID workspaceId) { return ApiResponse.success(query.list(workspaceId)); }
    @PostMapping("/{taxonomyId}/terms") @Operation(summary="Create taxonomy term")
    public ApiResponse<TaxonomyTermResponse> createTerm(@PathVariable UUID workspaceId, @PathVariable UUID taxonomyId, @Valid @RequestBody CreateTaxonomyTermRequest r) {
        return ApiResponse.success(createTerm.execute(new CreateTaxonomyTermCommand(workspaceId, taxonomyId, r.termCode(), r.label(), r.parentTermId())));
    }
    @GetMapping("/{taxonomyId}/terms") @Operation(summary="List taxonomy terms")
    public ApiResponse<List<TaxonomyTermResponse>> listTerms(@PathVariable UUID workspaceId, @PathVariable UUID taxonomyId) {
        return ApiResponse.success(query.listTerms(workspaceId, taxonomyId));
    }
}
