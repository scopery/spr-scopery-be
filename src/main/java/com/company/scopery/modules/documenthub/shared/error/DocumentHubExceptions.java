package com.company.scopery.modules.documenthub.shared.error;
import com.company.scopery.common.exception.AppException; import java.util.Map; import java.util.UUID;
public final class DocumentHubExceptions {
    private DocumentHubExceptions() {}
    public static AppException documentNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.DOCUMENT_NOT_FOUND, "Document not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException folderNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.FOLDER_NOT_FOUND, "Folder not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException folderAlreadyArchived(UUID id) { return new AppException(DocumentHubErrorCatalog.FOLDER_ALREADY_ARCHIVED, "Folder already archived: "+id, Map.of("id", id==null?"":id)); }
    public static AppException shareNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.SHARE_NOT_FOUND, "Document share not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException versionNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.VERSION_NOT_FOUND, "Version not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException templateNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.TEMPLATE_NOT_FOUND, "Template not found: "+id, Map.of("id", id==null?"":id)); }
    public static AppException jobNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.JOB_NOT_FOUND, "Job not found: "+id, Map.of("id", id)); }
    public static AppException immutable(UUID id) { return new AppException(DocumentHubErrorCatalog.DOCUMENT_IMMUTABLE, "Immutable: "+id, Map.of("id", id)); }
    public static AppException accessDenied() { return new AppException(DocumentHubErrorCatalog.DOCUMENT_ACCESS_DENIED); }
    public static AppException projectArchived(UUID id) { return new AppException(DocumentHubErrorCatalog.PROJECT_ARCHIVED, "Project archived: "+id, Map.of("projectId", id)); }
    public static AppException titleRequired() { return new AppException(DocumentHubErrorCatalog.TITLE_REQUIRED); }
    // Native editor exceptions
    public static AppException contentNotFound(UUID documentId) { return new AppException(DocumentHubErrorCatalog.CONTENT_NOT_FOUND, "No native content for document: "+documentId, Map.of("documentId", documentId)); }
    public static AppException contentNotSupported(UUID documentId) { return new AppException(DocumentHubErrorCatalog.CONTENT_NOT_SUPPORTED, "FILE document does not support native content: "+documentId, Map.of("documentId", documentId)); }
    public static AppException contentInvalid(String reason) { return new AppException(DocumentHubErrorCatalog.CONTENT_INVALID, reason, Map.of("reason", reason)); }
    public static AppException contentTooLarge(long actualBytes, long maxBytes) { return new AppException(DocumentHubErrorCatalog.CONTENT_TOO_LARGE, "AST size "+actualBytes+" bytes exceeds limit "+maxBytes, Map.of("actualBytes", actualBytes, "maxBytes", maxBytes)); }
    public static AppException contentRevisionConflict(long submitted, long current) { return new AppException(DocumentHubErrorCatalog.CONTENT_OPTIMISTIC_LOCK_CONFLICT, "Revision conflict: submitted base "+submitted+", current "+current, Map.of("submittedBaseRevisionNo", submitted, "currentRevisionNo", current)); }
    public static AppException blockLimitExceeded(int count, int max) { return new AppException(DocumentHubErrorCatalog.BLOCK_LIMIT_EXCEEDED, "Block count "+count+" exceeds max "+max, Map.of("count", count, "max", max)); }
    public static AppException blockDepthExceeded(int depth, int max) { return new AppException(DocumentHubErrorCatalog.BLOCK_DEPTH_EXCEEDED, "Block depth "+depth+" exceeds max "+max, Map.of("depth", depth, "max", max)); }
    public static AppException duplicateBlockId(String blockId) { return new AppException(DocumentHubErrorCatalog.DUPLICATE_BLOCK_ID, "Duplicate block ID: "+blockId, Map.of("blockId", blockId)); }
    public static AppException documentLockedForEdit(UUID documentId) { return new AppException(DocumentHubErrorCatalog.DOCUMENT_LOCKED_FOR_EDIT, "Document is locked: "+documentId, Map.of("documentId", documentId)); }
    public static AppException documentFinalized(UUID documentId) { return new AppException(DocumentHubErrorCatalog.DOCUMENT_FINALIZED, "Document is finalized: "+documentId, Map.of("documentId", documentId)); }
    public static AppException documentArchivedForEdit(UUID documentId) { return new AppException(DocumentHubErrorCatalog.DOCUMENT_ARCHIVED, "Document is archived: "+documentId, Map.of("documentId", documentId)); }
    public static AppException tableInvalid(String reason) { return new AppException(DocumentHubErrorCatalog.TABLE_INVALID, reason, Map.of("reason", reason)); }
    public static AppException tableLimitExceeded(String detail) { return new AppException(DocumentHubErrorCatalog.TABLE_LIMIT_EXCEEDED, detail, Map.of("detail", detail)); }
    public static AppException parentCycle(UUID documentId) { return new AppException(DocumentHubErrorCatalog.PARENT_CYCLE, "Sub-page cycle detected: "+documentId, Map.of("documentId", documentId)); }
    public static AppException parentScopeMismatch(UUID documentId, UUID parentId) { return new AppException(DocumentHubErrorCatalog.PARENT_SCOPE_MISMATCH, "Parent and child must be in same project", Map.of("documentId", documentId, "parentDocumentId", parentId)); }
    public static AppException attachmentNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.ATTACHMENT_NOT_FOUND, "Attachment not found: "+id, Map.of("id", id)); }
    public static AppException attachmentNotAvailable(UUID id) { return new AppException(DocumentHubErrorCatalog.ATTACHMENT_NOT_AVAILABLE, "Attachment not yet available: "+id, Map.of("id", id)); }
    public static AppException attachmentScopeMismatch(UUID id) { return new AppException(DocumentHubErrorCatalog.ATTACHMENT_SCOPE_MISMATCH, "Attachment belongs to a different document: "+id, Map.of("id", id)); }
    public static AppException mentionTypeDisabled(String type) { return new AppException(DocumentHubErrorCatalog.MENTION_TYPE_DISABLED, "Mention type not enabled: "+type, Map.of("resourceType", type)); }
    public static AppException mentionInsertDenied(String ref) { return new AppException(DocumentHubErrorCatalog.MENTION_INSERT_DENIED, "Cannot mention resource: "+ref, Map.of("resourceRef", ref)); }
    public static AppException commentThreadNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.COMMENT_THREAD_NOT_FOUND, "Comment thread not found: "+id, Map.of("id", id)); }
    public static AppException commentNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.COMMENT_NOT_FOUND, "Comment not found: "+id, Map.of("id", id)); }
    public static AppException commentAnchorInvalid(String blockId) { return new AppException(DocumentHubErrorCatalog.COMMENT_ANCHOR_INVALID, "Block not found in document: "+blockId, Map.of("blockId", blockId)); }
    public static AppException suggestionNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.SUGGESTION_NOT_FOUND, "Suggestion not found: "+id, Map.of("id", id)); }
    public static AppException suggestionBaseRevisionConflict(long base, long current) { return new AppException(DocumentHubErrorCatalog.SUGGESTION_BASE_REVISION_CONFLICT, "Suggestion base revision "+base+" conflicts with current "+current, Map.of("baseRevisionNo", base, "currentRevisionNo", current)); }
    public static AppException templateVariableMissing(String code) { return new AppException(DocumentHubErrorCatalog.TEMPLATE_VARIABLE_MISSING, "Missing required template variable: "+code, Map.of("variableCode", code)); }
    public static AppException syncedBlockNotFound(UUID id) { return new AppException(DocumentHubErrorCatalog.SYNCED_BLOCK_NOT_FOUND, "Synced block not found: "+id, Map.of("id", id)); }
    public static AppException syncedBlockCycleDetected(UUID id) { return new AppException(DocumentHubErrorCatalog.SYNCED_BLOCK_CYCLE_DETECTED, "Synced block recursive reference: "+id, Map.of("syncedBlockId", id)); }
    public static AppException clientVisibilityInvalid(int issueCount) { return new AppException(DocumentHubErrorCatalog.CLIENT_VISIBILITY_NOT_ALLOWED, "Document has "+issueCount+" client visibility issue(s)", Map.of("issueCount", issueCount)); }
    public static AppException baselineGuardBlocked(UUID documentId) { return new AppException(DocumentHubErrorCatalog.BASELINE_GUARD_BLOCKED, "Operation blocked by baseline guard: "+documentId, Map.of("documentId", documentId)); }
}
