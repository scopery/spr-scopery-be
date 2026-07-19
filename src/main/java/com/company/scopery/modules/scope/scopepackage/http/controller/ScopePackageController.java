package com.company.scopery.modules.scope.scopepackage.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.scopepackage.application.action.*;
import com.company.scopery.modules.scope.scopepackage.application.command.*;
import com.company.scopery.modules.scope.scopepackage.application.response.ScopePackageResponse;
import com.company.scopery.modules.scope.scopepackage.application.service.ScopePackageQueryService;
import com.company.scopery.modules.scope.scopepackage.http.request.CreateScopePackageRequest;
import com.company.scopery.modules.scope.scopepackage.http.request.ImportScopePackageFromQuoteRequest;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ScopeApiPaths.PACKAGES)
@Tag(name = "Scope - Packages")
public class ScopePackageController {
    private final CreateScopePackageAction create;
    private final ApproveScopePackageAction approve;
    private final MarkCurrentScopePackageAction markCurrent;
    private final ArchiveScopePackageAction archive;
    private final ImportScopePackageFromQuoteAction importFromQuote;
    private final ScopePackageQueryService query;

    public ScopePackageController(CreateScopePackageAction create, ApproveScopePackageAction approve,
                                  MarkCurrentScopePackageAction markCurrent, ArchiveScopePackageAction archive,
                                  ImportScopePackageFromQuoteAction importFromQuote, ScopePackageQueryService query) {
        this.create = create;
        this.approve = approve;
        this.markCurrent = markCurrent;
        this.archive = archive;
        this.importFromQuote = importFromQuote;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create scope package")
    public ApiResponse<ScopePackageResponse> create(@PathVariable UUID projectId,
                                                    @Valid @RequestBody CreateScopePackageRequest request) {
        return ApiResponse.success(create.execute(new CreateScopePackageCommand(
                projectId, request.code(), request.name(), request.description())));
    }

    @PostMapping("/import-from-quote")
    @Operation(summary = "Import scope package from quote version")
    public ApiResponse<ScopePackageResponse> importFromQuote(@PathVariable UUID projectId,
                                                               @Valid @RequestBody ImportScopePackageFromQuoteRequest request) {
        return ApiResponse.success(importFromQuote.execute(new ImportScopePackageFromQuoteCommand(
                projectId, request.quoteVersionId(), request.code(), request.name())));
    }

    @GetMapping
    @Operation(summary = "List scope packages")
    public ApiResponse<List<ScopePackageResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{packageId}")
    @Operation(summary = "Get scope package")
    public ApiResponse<ScopePackageResponse> get(@PathVariable UUID projectId, @PathVariable UUID packageId) {
        return ApiResponse.success(query.get(projectId, packageId));
    }

    @PostMapping("/{packageId}/approve")
    @Operation(summary = "Approve scope package")
    public ApiResponse<ScopePackageResponse> approve(@PathVariable UUID projectId, @PathVariable UUID packageId) {
        return ApiResponse.success(approve.execute(new ApproveScopePackageCommand(projectId, packageId)));
    }

    @PostMapping("/{packageId}/mark-current")
    @Operation(summary = "Mark approved scope package as current")
    public ApiResponse<ScopePackageResponse> markCurrent(@PathVariable UUID projectId, @PathVariable UUID packageId) {
        return ApiResponse.success(markCurrent.execute(new MarkCurrentScopePackageCommand(projectId, packageId)));
    }

    @PatchMapping("/{packageId}/archive")
    @Operation(summary = "Archive scope package")
    public ApiResponse<ScopePackageResponse> archive(@PathVariable UUID projectId, @PathVariable UUID packageId) {
        return ApiResponse.success(archive.execute(new ArchiveScopePackageCommand(projectId, packageId)));
    }
}
