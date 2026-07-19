package com.company.scopery.modules.quote.quoteversion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quote.quoteline.application.action.CreateQuoteLineAction;
import com.company.scopery.modules.quote.quoteline.application.action.DeleteQuoteLineAction;
import com.company.scopery.modules.quote.quoteline.application.action.ReorderQuoteLinesAction;
import com.company.scopery.modules.quote.quoteline.application.action.UpdateQuoteLineAction;
import com.company.scopery.modules.quote.quoteline.application.response.QuoteLineResponse;
import com.company.scopery.modules.quote.quoteline.http.request.CreateQuoteLineRequest;
import com.company.scopery.modules.quote.quoteline.http.request.ReorderQuoteLinesRequest;
import com.company.scopery.modules.quote.quoteline.http.request.UpdateQuoteLineRequest;
import com.company.scopery.modules.quote.quotesummary.application.response.QuoteSummaryResponse;
import com.company.scopery.modules.quote.quoteterm.application.action.CreateQuoteTermAction;
import com.company.scopery.modules.quote.quoteterm.application.action.DeleteQuoteTermAction;
import com.company.scopery.modules.quote.quoteterm.application.action.ReorderQuoteTermsAction;
import com.company.scopery.modules.quote.quoteterm.application.action.UpdateQuoteTermAction;
import com.company.scopery.modules.quote.quoteterm.application.response.QuoteTermResponse;
import com.company.scopery.modules.quote.quoteterm.http.request.CreateQuoteTermRequest;
import com.company.scopery.modules.quote.quoteterm.http.request.ReorderQuoteTermsRequest;
import com.company.scopery.modules.quote.quoteterm.http.request.UpdateQuoteTermRequest;
import com.company.scopery.modules.quote.quoteversion.application.action.ApproveQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.ArchiveQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.CreateQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.DuplicateQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.MarkAcceptedQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.MarkCurrentQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.RecalculateQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.RejectQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.SendQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.SolveTargetMarginAction;
import com.company.scopery.modules.quote.quoteversion.application.action.SubmitQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.action.UpdateQuoteVersionAction;
import com.company.scopery.modules.quote.quoteversion.application.command.CreateQuoteVersionCommand;
import com.company.scopery.modules.quote.quoteversion.application.command.UpdateQuoteVersionCommand;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
import com.company.scopery.modules.quote.quoteversion.application.response.SolveTargetMarginResponse;
import com.company.scopery.modules.quote.quoteversion.application.service.QuoteVersionQueryService;
import com.company.scopery.modules.quote.quoteversion.http.request.CreateQuoteVersionRequest;
import com.company.scopery.modules.quote.quoteversion.http.request.RejectQuoteVersionRequest;
import com.company.scopery.modules.quote.quoteversion.http.request.SolveTargetMarginRequest;
import com.company.scopery.modules.quote.quoteversion.http.request.UpdateQuoteVersionRequest;
import com.company.scopery.modules.quote.shared.constant.QuoteApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping(QuoteApiPaths.QUOTE_VERSIONS)
@Tag(name = "Quote - Versions")
public class QuoteVersionController {

    private final CreateQuoteVersionAction create;
    private final UpdateQuoteVersionAction update;
    private final DuplicateQuoteVersionAction duplicate;
    private final ArchiveQuoteVersionAction archive;
    private final SubmitQuoteVersionAction submit;
    private final ApproveQuoteVersionAction approve;
    private final RejectQuoteVersionAction reject;
    private final SendQuoteVersionAction send;
    private final MarkAcceptedQuoteVersionAction markAccepted;
    private final MarkCurrentQuoteVersionAction markCurrent;
    private final RecalculateQuoteVersionAction recalculate;
    private final SolveTargetMarginAction solve;
    private final CreateQuoteLineAction createLine;
    private final UpdateQuoteLineAction updateLine;
    private final DeleteQuoteLineAction deleteLine;
    private final ReorderQuoteLinesAction reorderLines;
    private final CreateQuoteTermAction createTerm;
    private final UpdateQuoteTermAction updateTerm;
    private final DeleteQuoteTermAction deleteTerm;
    private final ReorderQuoteTermsAction reorderTerms;
    private final QuoteVersionQueryService query;

    public QuoteVersionController(CreateQuoteVersionAction create, UpdateQuoteVersionAction update,
                                   DuplicateQuoteVersionAction duplicate, ArchiveQuoteVersionAction archive,
                                   SubmitQuoteVersionAction submit, ApproveQuoteVersionAction approve,
                                   RejectQuoteVersionAction reject, SendQuoteVersionAction send,
                                   MarkAcceptedQuoteVersionAction markAccepted,
                                   MarkCurrentQuoteVersionAction markCurrent,
                                   RecalculateQuoteVersionAction recalculate, SolveTargetMarginAction solve,
                                   CreateQuoteLineAction createLine, UpdateQuoteLineAction updateLine,
                                   DeleteQuoteLineAction deleteLine, ReorderQuoteLinesAction reorderLines,
                                   CreateQuoteTermAction createTerm, UpdateQuoteTermAction updateTerm,
                                   DeleteQuoteTermAction deleteTerm, ReorderQuoteTermsAction reorderTerms,
                                   QuoteVersionQueryService query) {
        this.create = create;
        this.update = update;
        this.duplicate = duplicate;
        this.archive = archive;
        this.submit = submit;
        this.approve = approve;
        this.reject = reject;
        this.send = send;
        this.markAccepted = markAccepted;
        this.markCurrent = markCurrent;
        this.recalculate = recalculate;
        this.solve = solve;
        this.createLine = createLine;
        this.updateLine = updateLine;
        this.deleteLine = deleteLine;
        this.reorderLines = reorderLines;
        this.createTerm = createTerm;
        this.updateTerm = updateTerm;
        this.deleteTerm = deleteTerm;
        this.reorderTerms = reorderTerms;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create quote version from finance scenario")
    public ApiResponse<QuoteVersionResponse> create(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                    @Valid @RequestBody CreateQuoteVersionRequest request) {
        return ApiResponse.success(create.execute(new CreateQuoteVersionCommand(
                projectId, quoteId, request.financeScenarioId(), request.pricingMethod(),
                request.costBaseMethod(), request.targetMarginPercent(), request.generateLinesFrom(),
                request.validUntil(), request.proposalTitle(), request.proposalNotes(),
                request.discountMethod(), request.discountPercent(), request.discountAmount(),
                request.discountReason())));
    }

    @GetMapping
    @Operation(summary = "List quote versions")
    public ApiResponse<List<QuoteVersionResponse>> list(@PathVariable UUID projectId, @PathVariable UUID quoteId) {
        return ApiResponse.success(query.list(projectId, quoteId));
    }

    @GetMapping("/{versionId}")
    @Operation(summary = "Get quote version")
    public ApiResponse<QuoteVersionResponse> get(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                 @PathVariable UUID versionId) {
        return ApiResponse.success(query.get(projectId, quoteId, versionId));
    }

    @PutMapping("/{versionId}")
    @Operation(summary = "Update draft quote version")
    public ApiResponse<QuoteVersionResponse> update(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                    @PathVariable UUID versionId,
                                                    @Valid @RequestBody UpdateQuoteVersionRequest request) {
        return ApiResponse.success(update.execute(new UpdateQuoteVersionCommand(
                projectId, quoteId, versionId, request.pricingMethod(), request.costBaseMethod(),
                request.targetMarginPercent(), request.validUntil(), request.proposalTitle(),
                request.proposalNotes(), request.discountMethod(), request.discountPercent(),
                request.discountAmount(), request.discountReason())));
    }

    @PostMapping("/{versionId}/duplicate")
    @Operation(summary = "Duplicate quote version into new DRAFT")
    public ApiResponse<QuoteVersionResponse> duplicate(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                        @PathVariable UUID versionId) {
        return ApiResponse.success(duplicate.execute(projectId, quoteId, versionId));
    }

    @PatchMapping("/{versionId}/archive")
    @Operation(summary = "Archive quote version")
    public ApiResponse<QuoteVersionResponse> archive(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId) {
        return ApiResponse.success(archive.execute(projectId, quoteId, versionId));
    }

    @GetMapping("/{versionId}/summary")
    @Operation(summary = "Get quote summary")
    public ApiResponse<QuoteSummaryResponse> summary(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId) {
        return ApiResponse.success(query.getSummary(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/recalculate")
    @Operation(summary = "Recalculate quote summary from lines + frozen finance snapshot")
    public ApiResponse<QuoteSummaryResponse> recalculate(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                         @PathVariable UUID versionId) {
        return ApiResponse.success(recalculate.execute(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/solve-target-margin")
    @Operation(summary = "Solve required contract value from target margin")
    public ApiResponse<SolveTargetMarginResponse> solve(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                        @PathVariable UUID versionId,
                                                        @Valid @RequestBody SolveTargetMarginRequest request) {
        return ApiResponse.success(solve.execute(projectId, quoteId, versionId,
                request.costBase(), request.targetMarginPercent(), request.currencyCode()));
    }

    @PostMapping("/{versionId}/submit")
    @Operation(summary = "Submit quote version")
    public ApiResponse<QuoteVersionResponse> submit(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                    @PathVariable UUID versionId) {
        return ApiResponse.success(submit.execute(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/approve")
    @Operation(summary = "Approve quote version")
    public ApiResponse<QuoteVersionResponse> approve(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId) {
        return ApiResponse.success(approve.execute(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/reject")
    @Operation(summary = "Reject quote version")
    public ApiResponse<QuoteVersionResponse> reject(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                    @PathVariable UUID versionId,
                                                    @Valid @RequestBody RejectQuoteVersionRequest request) {
        return ApiResponse.success(reject.execute(projectId, quoteId, versionId, request.reason()));
    }

    @PostMapping("/{versionId}/send")
    @Operation(summary = "Mark quote version as sent")
    public ApiResponse<QuoteVersionResponse> send(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                  @PathVariable UUID versionId) {
        return ApiResponse.success(send.execute(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/mark-accepted")
    @Operation(summary = "Mark quote version as accepted (no contract)")
    public ApiResponse<QuoteVersionResponse> markAccepted(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                          @PathVariable UUID versionId) {
        return ApiResponse.success(markAccepted.execute(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/mark-current")
    @Operation(summary = "Mark quote version as current")
    public ApiResponse<QuoteVersionResponse> markCurrent(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                         @PathVariable UUID versionId) {
        return ApiResponse.success(markCurrent.execute(projectId, quoteId, versionId));
    }

    @PostMapping("/{versionId}/lines")
    @Operation(summary = "Create quote line")
    public ApiResponse<QuoteLineResponse> createLine(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId,
                                                     @Valid @RequestBody CreateQuoteLineRequest request) {
        return ApiResponse.success(createLine.execute(projectId, quoteId, versionId, request.lineType(),
                request.name(), request.description(), request.quantity(), request.unitPrice(),
                request.displayOrder(), request.clientVisible(), request.internalNote(),
                request.sourceProjectPhaseId()));
    }

    @GetMapping("/{versionId}/lines")
    @Operation(summary = "List quote lines")
    public ApiResponse<List<QuoteLineResponse>> listLines(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                          @PathVariable UUID versionId) {
        return ApiResponse.success(query.listLines(projectId, quoteId, versionId));
    }

    @GetMapping("/{versionId}/lines/{lineId}")
    @Operation(summary = "Get quote line")
    public ApiResponse<QuoteLineResponse> getLine(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                  @PathVariable UUID versionId, @PathVariable UUID lineId) {
        return ApiResponse.success(query.getLine(projectId, quoteId, versionId, lineId));
    }

    @PutMapping("/{versionId}/lines/{lineId}")
    @Operation(summary = "Update quote line")
    public ApiResponse<QuoteLineResponse> updateLine(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId, @PathVariable UUID lineId,
                                                     @Valid @RequestBody UpdateQuoteLineRequest request) {
        return ApiResponse.success(updateLine.execute(projectId, quoteId, versionId, lineId, request.lineType(),
                request.name(), request.description(), request.quantity(), request.unitPrice(),
                request.displayOrder(), request.clientVisible(), request.internalNote()));
    }

    @DeleteMapping("/{versionId}/lines/{lineId}")
    @Operation(summary = "Delete quote line")
    public ApiResponse<Void> deleteLine(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                        @PathVariable UUID versionId, @PathVariable UUID lineId) {
        deleteLine.execute(projectId, quoteId, versionId, lineId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{versionId}/lines/reorder")
    @Operation(summary = "Reorder quote lines")
    public ApiResponse<List<QuoteLineResponse>> reorderLines(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                             @PathVariable UUID versionId,
                                                             @Valid @RequestBody ReorderQuoteLinesRequest request) {
        return ApiResponse.success(reorderLines.execute(projectId, quoteId, versionId, request.lineIds()));
    }

    @PostMapping("/{versionId}/terms")
    @Operation(summary = "Create quote term")
    public ApiResponse<QuoteTermResponse> createTerm(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId,
                                                     @Valid @RequestBody CreateQuoteTermRequest request) {
        return ApiResponse.success(createTerm.execute(projectId, quoteId, versionId, request.termType(),
                request.title(), request.content(), request.displayOrder(), request.clientVisible()));
    }

    @GetMapping("/{versionId}/terms")
    @Operation(summary = "List quote terms")
    public ApiResponse<List<QuoteTermResponse>> listTerms(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                          @PathVariable UUID versionId) {
        return ApiResponse.success(query.listTerms(projectId, quoteId, versionId));
    }

    @PutMapping("/{versionId}/terms/{termId}")
    @Operation(summary = "Update quote term")
    public ApiResponse<QuoteTermResponse> updateTerm(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                     @PathVariable UUID versionId, @PathVariable UUID termId,
                                                     @Valid @RequestBody UpdateQuoteTermRequest request) {
        return ApiResponse.success(updateTerm.execute(projectId, quoteId, versionId, termId, request.termType(),
                request.title(), request.content(), request.displayOrder(), request.clientVisible()));
    }

    @DeleteMapping("/{versionId}/terms/{termId}")
    @Operation(summary = "Delete quote term")
    public ApiResponse<Void> deleteTerm(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                        @PathVariable UUID versionId, @PathVariable UUID termId) {
        deleteTerm.execute(projectId, quoteId, versionId, termId);
        return ApiResponse.success(null);
    }

    @PutMapping("/{versionId}/terms/reorder")
    @Operation(summary = "Reorder quote terms")
    public ApiResponse<List<QuoteTermResponse>> reorderTerms(@PathVariable UUID projectId, @PathVariable UUID quoteId,
                                                             @PathVariable UUID versionId,
                                                             @Valid @RequestBody ReorderQuoteTermsRequest request) {
        return ApiResponse.success(reorderTerms.execute(projectId, quoteId, versionId, request.termIds()));
    }
}
