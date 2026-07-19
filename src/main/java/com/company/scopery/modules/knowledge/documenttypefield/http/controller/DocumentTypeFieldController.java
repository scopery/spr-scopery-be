package com.company.scopery.modules.knowledge.documenttypefield.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.ActivateDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.ArchiveDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.CreateDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.DeactivateDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.ReorderDocumentTypeFieldsAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.action.UpdateDocumentTypeFieldAction;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.CreateDocumentTypeFieldCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.ReorderDocumentTypeFieldsCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.command.UpdateDocumentTypeFieldCommand;
import com.company.scopery.modules.knowledge.documenttypefield.application.response.DocumentTypeFieldResponse;
import com.company.scopery.modules.knowledge.documenttypefield.application.service.DocumentTypeFieldQueryService;
import com.company.scopery.modules.knowledge.documenttypefield.http.request.CreateDocumentTypeFieldRequest;
import com.company.scopery.modules.knowledge.documenttypefield.http.request.ReorderDocumentTypeFieldsRequest;
import com.company.scopery.modules.knowledge.documenttypefield.http.request.UpdateDocumentTypeFieldRequest;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Knowledge - Document Type Fields", description = "Metadata field schema for document types")
@RestController
@RequestMapping(KnowledgeApiPaths.DOCUMENT_TYPES + "/{documentTypeId}/fields")
public class DocumentTypeFieldController {

    private final CreateDocumentTypeFieldAction createAction;
    private final UpdateDocumentTypeFieldAction updateAction;
    private final ActivateDocumentTypeFieldAction activateAction;
    private final DeactivateDocumentTypeFieldAction deactivateAction;
    private final ArchiveDocumentTypeFieldAction archiveAction;
    private final ReorderDocumentTypeFieldsAction reorderAction;
    private final DocumentTypeFieldQueryService queryService;

    public DocumentTypeFieldController(CreateDocumentTypeFieldAction createAction,
                                       UpdateDocumentTypeFieldAction updateAction,
                                       ActivateDocumentTypeFieldAction activateAction,
                                       DeactivateDocumentTypeFieldAction deactivateAction,
                                       ArchiveDocumentTypeFieldAction archiveAction,
                                       ReorderDocumentTypeFieldsAction reorderAction,
                                       DocumentTypeFieldQueryService queryService) {
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
        this.reorderAction = reorderAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Create a document type field")
    @PostMapping
    public ResponseEntity<ApiResponse<DocumentTypeFieldResponse>> create(
            @PathVariable UUID documentTypeId,
            @Valid @RequestBody CreateDocumentTypeFieldRequest request) {
        DocumentTypeFieldResponse response = createAction.execute(new CreateDocumentTypeFieldCommand(
                documentTypeId, request.fieldKey(), request.label(), request.description(),
                request.dataType(), request.required(), request.systemField(), request.optionsJson(),
                request.validationJson(), request.defaultValueJson(), request.displayOrder()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "List fields for a document type")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentTypeFieldResponse>>> list(@PathVariable UUID documentTypeId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.listFields(documentTypeId)));
    }

    @Operation(summary = "Get a document type field")
    @GetMapping("/{fieldId}")
    public ResponseEntity<ApiResponse<DocumentTypeFieldResponse>> get(
            @PathVariable UUID documentTypeId, @PathVariable UUID fieldId) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getField(documentTypeId, fieldId)));
    }

    @Operation(summary = "Update a document type field")
    @PutMapping("/{fieldId}")
    public ResponseEntity<ApiResponse<DocumentTypeFieldResponse>> update(
            @PathVariable UUID documentTypeId,
            @PathVariable UUID fieldId,
            @Valid @RequestBody UpdateDocumentTypeFieldRequest request) {
        return ResponseEntity.ok(ApiResponse.success(updateAction.execute(new UpdateDocumentTypeFieldCommand(
                documentTypeId, fieldId, request.label(), request.description(), request.dataType(),
                request.required(), request.optionsJson(), request.validationJson(),
                request.defaultValueJson(), request.displayOrder()))));
    }

    @Operation(summary = "Activate a document type field")
    @PatchMapping("/{fieldId}/activate")
    public ResponseEntity<ApiResponse<DocumentTypeFieldResponse>> activate(
            @PathVariable UUID documentTypeId, @PathVariable UUID fieldId) {
        return ResponseEntity.ok(ApiResponse.success(activateAction.execute(documentTypeId, fieldId)));
    }

    @Operation(summary = "Deactivate a document type field")
    @PatchMapping("/{fieldId}/deactivate")
    public ResponseEntity<ApiResponse<DocumentTypeFieldResponse>> deactivate(
            @PathVariable UUID documentTypeId, @PathVariable UUID fieldId) {
        return ResponseEntity.ok(ApiResponse.success(deactivateAction.execute(documentTypeId, fieldId)));
    }

    @Operation(summary = "Archive a document type field")
    @PatchMapping("/{fieldId}/archive")
    public ResponseEntity<ApiResponse<DocumentTypeFieldResponse>> archive(
            @PathVariable UUID documentTypeId, @PathVariable UUID fieldId) {
        return ResponseEntity.ok(ApiResponse.success(archiveAction.execute(documentTypeId, fieldId)));
    }

    @Operation(summary = "Reorder document type fields")
    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse<List<DocumentTypeFieldResponse>>> reorder(
            @PathVariable UUID documentTypeId,
            @Valid @RequestBody ReorderDocumentTypeFieldsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(reorderAction.execute(
                new ReorderDocumentTypeFieldsCommand(documentTypeId, request.orderedFieldIds()))));
    }
}
