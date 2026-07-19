package com.company.scopery.modules.documenthub.attachment.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.attachment.application.action.CompleteAttachmentUploadAction;
import com.company.scopery.modules.documenthub.attachment.application.action.CreateAttachmentPresignedUploadAction;
import com.company.scopery.modules.documenthub.attachment.application.command.CompleteAttachmentUploadCommand;
import com.company.scopery.modules.documenthub.attachment.application.command.CreateAttachmentPresignedUploadCommand;
import com.company.scopery.modules.documenthub.attachment.application.response.DocumentAttachmentResponse;
import com.company.scopery.modules.documenthub.attachment.application.service.AttachmentQueryService;
import com.company.scopery.modules.documenthub.attachment.http.request.CreateAttachmentRequest;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Document Hub - Attachments")
public class DocumentAttachmentController {

    private final CreateAttachmentPresignedUploadAction createPresignedUpload;
    private final CompleteAttachmentUploadAction completeUpload;
    private final AttachmentQueryService query;

    public DocumentAttachmentController(CreateAttachmentPresignedUploadAction createPresignedUpload,
                                         CompleteAttachmentUploadAction completeUpload,
                                         AttachmentQueryService query) {
        this.createPresignedUpload = createPresignedUpload;
        this.completeUpload = completeUpload;
        this.query = query;
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_ATTACHMENTS)
    @Operation(summary = "List document attachments")
    public ApiResponse<List<DocumentAttachmentResponse>> list(@PathVariable UUID projectId,
                                                               @PathVariable UUID documentId) {
        return ApiResponse.success(query.listByDocument(projectId, documentId));
    }

    @GetMapping(DocumentHubApiPaths.DOCUMENT_ATTACHMENTS + "/{attachmentId}")
    @Operation(summary = "Get attachment")
    public ApiResponse<DocumentAttachmentResponse> get(@PathVariable UUID projectId,
                                                        @PathVariable UUID documentId,
                                                        @PathVariable UUID attachmentId) {
        return ApiResponse.success(query.get(projectId, documentId, attachmentId));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_ATTACHMENTS)
    @Operation(summary = "Create presigned upload URL for attachment")
    public ApiResponse<DocumentAttachmentResponse> createPresignedUpload(@PathVariable UUID projectId,
                                                                          @PathVariable UUID documentId,
                                                                          @Valid @RequestBody CreateAttachmentRequest r) {
        return ApiResponse.success(createPresignedUpload.execute(new CreateAttachmentPresignedUploadCommand(
                projectId, documentId, r.blockId(), r.fileName(), r.mediaType(), r.fileSizeBytes())));
    }

    @PostMapping(DocumentHubApiPaths.DOCUMENT_ATTACHMENTS + "/{attachmentId}/complete-upload")
    @Operation(summary = "Complete attachment upload — verify and mark AVAILABLE")
    public ApiResponse<DocumentAttachmentResponse> completeUpload(@PathVariable UUID projectId,
                                                                   @PathVariable UUID documentId,
                                                                   @PathVariable UUID attachmentId) {
        return ApiResponse.success(completeUpload.execute(new CompleteAttachmentUploadCommand(
                projectId, documentId, attachmentId)));
    }
}
