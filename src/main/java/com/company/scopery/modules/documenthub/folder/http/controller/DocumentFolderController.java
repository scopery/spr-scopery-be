package com.company.scopery.modules.documenthub.folder.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.documenthub.folder.application.action.*;
import com.company.scopery.modules.documenthub.folder.application.command.ArchiveDocumentFolderCommand;
import com.company.scopery.modules.documenthub.folder.application.command.CreateDocumentFolderCommand;
import com.company.scopery.modules.documenthub.folder.application.response.DocumentFolderResponse;
import com.company.scopery.modules.documenthub.folder.application.service.DocumentFolderQueryService;
import com.company.scopery.modules.documenthub.folder.http.request.CreateDocumentFolderRequest;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(DocumentHubApiPaths.FOLDERS)
@Tag(name = "Document Hub - Folders")
public class DocumentFolderController {
    private final CreateDocumentFolderAction create;
    private final ArchiveDocumentFolderAction archive;
    private final DocumentFolderQueryService query;
    public DocumentFolderController(CreateDocumentFolderAction create, ArchiveDocumentFolderAction archive, DocumentFolderQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Create folder")
    public ApiResponse<DocumentFolderResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateDocumentFolderRequest r) {
        return ApiResponse.success(create.execute(new CreateDocumentFolderCommand(projectId, r.parentFolderId(), r.name(), r.description(), r.sortOrder())));
    }
    @GetMapping @Operation(summary = "List folders")
    public ApiResponse<List<DocumentFolderResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
    @GetMapping("/{folderId}") @Operation(summary = "Get folder")
    public ApiResponse<DocumentFolderResponse> get(@PathVariable UUID projectId, @PathVariable UUID folderId) {
        return ApiResponse.success(query.get(projectId, folderId));
    }
    @PatchMapping("/{folderId}/archive") @Operation(summary = "Archive folder")
    public ApiResponse<DocumentFolderResponse> archive(@PathVariable UUID projectId, @PathVariable UUID folderId) {
        return ApiResponse.success(archive.execute(new ArchiveDocumentFolderCommand(projectId, folderId)));
    }
}
