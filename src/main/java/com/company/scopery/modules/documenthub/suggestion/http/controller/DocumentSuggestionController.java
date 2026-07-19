package com.company.scopery.modules.documenthub.suggestion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import com.company.scopery.modules.documenthub.suggestion.application.action.AcceptSuggestionAction;
import com.company.scopery.modules.documenthub.suggestion.application.action.CreateSuggestionAction;
import com.company.scopery.modules.documenthub.suggestion.application.action.RejectSuggestionAction;
import com.company.scopery.modules.documenthub.suggestion.application.command.AcceptSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.application.command.CreateSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.application.command.RejectSuggestionCommand;
import com.company.scopery.modules.documenthub.suggestion.application.response.SuggestionResponse;
import com.company.scopery.modules.documenthub.suggestion.application.service.DocumentSuggestionQueryService;
import com.company.scopery.modules.documenthub.suggestion.http.request.CreateSuggestionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Document Hub - Suggestions")
public class DocumentSuggestionController {

    private final CreateSuggestionAction createSuggestion;
    private final AcceptSuggestionAction acceptSuggestion;
    private final RejectSuggestionAction rejectSuggestion;
    private final DocumentSuggestionQueryService query;

    public DocumentSuggestionController(CreateSuggestionAction createSuggestion,
                                         AcceptSuggestionAction acceptSuggestion,
                                         RejectSuggestionAction rejectSuggestion,
                                         DocumentSuggestionQueryService query) {
        this.createSuggestion = createSuggestion;
        this.acceptSuggestion = acceptSuggestion;
        this.rejectSuggestion = rejectSuggestion;
        this.query = query;
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_SUGGESTIONS)
    @Operation(summary = "List suggestions for a document")
    public ApiResponse<List<SuggestionResponse>> list(@PathVariable UUID projectId,
                                                       @PathVariable UUID documentId) {
        return ApiResponse.success(query.listByDocument(projectId, documentId));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_SUGGESTIONS + "/{suggestionId}")
    @Operation(summary = "Get a suggestion")
    public ApiResponse<SuggestionResponse> get(@PathVariable UUID projectId,
                                                @PathVariable UUID documentId,
                                                @PathVariable UUID suggestionId) {
        return ApiResponse.success(query.get(projectId, documentId, suggestionId));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_SUGGESTIONS)
    @Operation(summary = "Create a suggestion with operations")
    public ApiResponse<SuggestionResponse> create(@PathVariable UUID projectId,
                                                   @PathVariable UUID documentId,
                                                   @Valid @RequestBody CreateSuggestionRequest r) {
        var ops = r.operations().stream()
                .map(o -> new CreateSuggestionCommand.OperationItem(o.opType(), o.blockId(), o.path(), o.value(), o.ordinal()))
                .toList();
        return ApiResponse.success(createSuggestion.execute(new CreateSuggestionCommand(
                projectId, documentId, r.targetRevisionNo(), r.description(), ops)));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_SUGGESTIONS + "/{suggestionId}/accept")
    @Operation(summary = "Accept a suggestion and apply it to the document content")
    public ApiResponse<SuggestionResponse> accept(@PathVariable UUID projectId,
                                                   @PathVariable UUID documentId,
                                                   @PathVariable UUID suggestionId) {
        return ApiResponse.success(acceptSuggestion.execute(new AcceptSuggestionCommand(projectId, documentId, suggestionId)));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_SUGGESTIONS + "/{suggestionId}/reject")
    @Operation(summary = "Reject a suggestion")
    public ApiResponse<SuggestionResponse> reject(@PathVariable UUID projectId,
                                                   @PathVariable UUID documentId,
                                                   @PathVariable UUID suggestionId) {
        return ApiResponse.success(rejectSuggestion.execute(new RejectSuggestionCommand(projectId, documentId, suggestionId)));
    }
}
