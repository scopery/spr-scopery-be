package com.company.scopery.modules.documenthub.version.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import com.company.scopery.modules.documenthub.version.application.action.CompleteUploadAction;
import com.company.scopery.modules.documenthub.version.application.action.CreatePresignedDownloadAction;
import com.company.scopery.modules.documenthub.version.application.action.CreatePresignedUploadAction;
import com.company.scopery.modules.documenthub.version.application.action.UploadDocumentVersionAction;
import com.company.scopery.modules.documenthub.version.application.command.CompleteUploadCommand;
import com.company.scopery.modules.documenthub.version.application.command.CreatePresignedDownloadCommand;
import com.company.scopery.modules.documenthub.version.application.command.CreatePresignedUploadCommand;
import com.company.scopery.modules.documenthub.version.application.command.UploadDocumentVersionCommand;
import com.company.scopery.modules.documenthub.version.application.response.DocumentVersionResponse;
import com.company.scopery.modules.documenthub.version.application.response.PresignedDownloadResponse;
import com.company.scopery.modules.documenthub.version.application.response.PresignedUploadResponse;
import com.company.scopery.modules.documenthub.version.application.service.DocumentVersionQueryService;
import com.company.scopery.modules.documenthub.version.http.request.CompleteUploadRequest;
import com.company.scopery.modules.documenthub.version.http.request.CreatePresignedUploadRequest;
import com.company.scopery.modules.documenthub.version.http.request.UploadDocumentVersionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(DocumentHubApiPaths.DOCUMENT_VERSIONS)
@Tag(name = "Document Hub - Versions")
public class DocumentVersionController {

    private final UploadDocumentVersionAction upload;
    private final CreatePresignedUploadAction createPresignedUpload;
    private final CompleteUploadAction completeUpload;
    private final CreatePresignedDownloadAction createPresignedDownload;
    private final DocumentVersionQueryService query;

    public DocumentVersionController(UploadDocumentVersionAction upload,
                                     CreatePresignedUploadAction createPresignedUpload,
                                     CompleteUploadAction completeUpload,
                                     CreatePresignedDownloadAction createPresignedDownload,
                                     DocumentVersionQueryService query) {
        this.upload = upload;
        this.createPresignedUpload = createPresignedUpload;
        this.completeUpload = completeUpload;
        this.createPresignedDownload = createPresignedDownload;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Upload document version (legacy: direct storage_key reference)")
    public ApiResponse<DocumentVersionResponse> upload(@PathVariable UUID projectId, @PathVariable UUID documentId,
                                                       @Valid @RequestBody UploadDocumentVersionRequest r) {
        return ApiResponse.success(upload.execute(new UploadDocumentVersionCommand(
                projectId, documentId, r.storageKey(), r.fileName(), r.contentType(),
                r.fileSizeBytes(), r.checksum(), r.changeNotes())));
    }

    @PostMapping("/presigned-upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create presigned URL for direct-to-storage upload")
    public ApiResponse<PresignedUploadResponse> presignedUpload(@PathVariable UUID projectId, @PathVariable UUID documentId,
                                                                 @Valid @RequestBody CreatePresignedUploadRequest r) {
        return ApiResponse.success(createPresignedUpload.execute(new CreatePresignedUploadCommand(
                projectId, documentId, r.fileName(), r.contentType(), r.changeNotes())));
    }

    @PostMapping("/{versionId}/complete-upload")
    @Operation(summary = "Verify and mark a presigned upload as AVAILABLE")
    public ApiResponse<DocumentVersionResponse> completeUpload(@PathVariable UUID projectId, @PathVariable UUID documentId,
                                                               @PathVariable UUID versionId,
                                                               @RequestBody(required = false) CompleteUploadRequest r) {
        String checksum = r != null ? r.checksum() : null;
        Long size = r != null ? r.fileSizeBytes() : null;
        return ApiResponse.success(completeUpload.execute(new CompleteUploadCommand(projectId, documentId, versionId, checksum, size)));
    }

    @PostMapping("/{versionId}/presigned-download")
    @Operation(summary = "Generate presigned download URL for an AVAILABLE version")
    public ApiResponse<PresignedDownloadResponse> presignedDownload(@PathVariable UUID projectId,
                                                                     @PathVariable UUID versionId) {
        return ApiResponse.success(createPresignedDownload.execute(new CreatePresignedDownloadCommand(projectId, versionId)));
    }

    @GetMapping
    @Operation(summary = "List versions")
    public ApiResponse<List<DocumentVersionResponse>> list(@PathVariable UUID projectId, @PathVariable UUID documentId) {
        return ApiResponse.success(query.list(projectId, documentId));
    }

    @GetMapping("/{versionId}")
    @Operation(summary = "Get version metadata")
    public ApiResponse<DocumentVersionResponse> get(@PathVariable UUID projectId, @PathVariable UUID versionId) {
        return ApiResponse.success(query.get(projectId, versionId));
    }
}
