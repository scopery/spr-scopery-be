package com.company.scopery.modules.quote.quote.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quote.quote.application.action.ArchiveQuoteAction;
import com.company.scopery.modules.quote.quote.application.action.CreateQuoteAction;
import com.company.scopery.modules.quote.quote.application.action.UpdateQuoteAction;
import com.company.scopery.modules.quote.quote.application.command.CreateQuoteCommand;
import com.company.scopery.modules.quote.quote.application.command.UpdateQuoteCommand;
import com.company.scopery.modules.quote.quote.application.response.QuoteResponse;
import com.company.scopery.modules.quote.quote.application.service.QuoteQueryService;
import com.company.scopery.modules.quote.quote.http.request.CreateQuoteRequest;
import com.company.scopery.modules.quote.quote.http.request.UpdateQuoteRequest;
import com.company.scopery.modules.quote.shared.constant.QuoteApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(QuoteApiPaths.QUOTES)
@Tag(name = "Quote - Quotes")
public class QuoteController {

    private final CreateQuoteAction create;
    private final UpdateQuoteAction update;
    private final ArchiveQuoteAction archive;
    private final QuoteQueryService query;

    public QuoteController(CreateQuoteAction create, UpdateQuoteAction update,
                            ArchiveQuoteAction archive, QuoteQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create quote shell from finance scenario")
    public ApiResponse<QuoteResponse> create(@PathVariable UUID projectId,
                                             @Valid @RequestBody CreateQuoteRequest request) {
        return ApiResponse.success(create.execute(new CreateQuoteCommand(
                projectId, request.code(), request.title(), request.description(),
                request.sourceFinanceScenarioId(), request.clientName(), request.clientCompany(),
                request.clientEmail(), request.clientContactName(), request.clientReference())));
    }

    @GetMapping
    @Operation(summary = "List quotes for project")
    public ApiResponse<List<QuoteResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{quoteId}")
    @Operation(summary = "Get quote")
    public ApiResponse<QuoteResponse> get(@PathVariable UUID projectId, @PathVariable UUID quoteId) {
        return ApiResponse.success(query.get(projectId, quoteId));
    }

    @PutMapping("/{quoteId}")
    @Operation(summary = "Update quote")
    public ApiResponse<QuoteResponse> update(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                             @Valid @RequestBody UpdateQuoteRequest request) {
        return ApiResponse.success(update.execute(new UpdateQuoteCommand(
                projectId, quoteId, request.title(), request.description(),
                request.clientName(), request.clientCompany(), request.clientEmail(),
                request.clientContactName(), request.clientReference())));
    }

    @PatchMapping("/{quoteId}/archive")
    @Operation(summary = "Archive quote")
    public ApiResponse<QuoteResponse> archive(@PathVariable UUID projectId, @PathVariable UUID quoteId) {
        return ApiResponse.success(archive.execute(projectId, quoteId));
    }
}
