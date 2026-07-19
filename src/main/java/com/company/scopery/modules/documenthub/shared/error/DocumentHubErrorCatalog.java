package com.company.scopery.modules.documenthub.shared.error;
import com.company.scopery.common.exception.ErrorCatalog; import org.springframework.http.HttpStatus;
public enum DocumentHubErrorCatalog implements ErrorCatalog {
    DOCUMENT_NOT_FOUND("DOCUMENT_NOT_FOUND", "Document not found", HttpStatus.NOT_FOUND),
    FOLDER_NOT_FOUND("FOLDER_NOT_FOUND", "Folder not found", HttpStatus.NOT_FOUND),
    FOLDER_ALREADY_ARCHIVED("FOLDER_ALREADY_ARCHIVED", "Folder is already archived", HttpStatus.UNPROCESSABLE_ENTITY),
    SHARE_NOT_FOUND("SHARE_NOT_FOUND", "Document share not found", HttpStatus.NOT_FOUND),
    VERSION_NOT_FOUND("VERSION_NOT_FOUND", "Document version not found", HttpStatus.NOT_FOUND),
    TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND", "Document template not found", HttpStatus.NOT_FOUND),
    JOB_NOT_FOUND("GENERATED_JOB_NOT_FOUND", "Generated document job not found", HttpStatus.NOT_FOUND),
    DOCUMENT_IMMUTABLE("DOCUMENT_IMMUTABLE", "Document is immutable in current status", HttpStatus.UNPROCESSABLE_ENTITY),
    DOCUMENT_ACCESS_DENIED("DOCUMENT_ACCESS_DENIED", "Document access denied", HttpStatus.FORBIDDEN),
    PROJECT_ARCHIVED("DOCUMENT_PROJECT_ARCHIVED", "Cannot modify documents for archived project", HttpStatus.UNPROCESSABLE_ENTITY),
    TITLE_REQUIRED("DOCUMENT_TITLE_REQUIRED", "Document title is required", HttpStatus.BAD_REQUEST),
    // Native editor errors — content and mode
    CONTENT_NOT_FOUND("DOCUMENT_CONTENT_NOT_FOUND", "Document has no native content yet", HttpStatus.NOT_FOUND),
    CONTENT_NOT_SUPPORTED("DOCUMENT_NATIVE_CONTENT_NOT_SUPPORTED", "FILE documents do not support native content", HttpStatus.CONFLICT),
    CONTENT_MODE_INVALID("DOCUMENT_CONTENT_MODE_INVALID", "Invalid document content mode", HttpStatus.BAD_REQUEST),
    CONTENT_SCHEMA_UNSUPPORTED("DOCUMENT_CONTENT_SCHEMA_UNSUPPORTED", "Unsupported AST schema version", HttpStatus.UNPROCESSABLE_ENTITY),
    CONTENT_INVALID("DOCUMENT_CONTENT_INVALID", "Document AST is invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    CONTENT_TOO_LARGE("DOCUMENT_CONTENT_TOO_LARGE", "Document AST exceeds maximum size", HttpStatus.PAYLOAD_TOO_LARGE),
    CONTENT_OPTIMISTIC_LOCK_CONFLICT("DOCUMENT_CONTENT_REVISION_CONFLICT", "The document was updated by another user", HttpStatus.CONFLICT),
    CONTENT_IDEMPOTENCY_CONFLICT("IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_PAYLOAD", "Idempotency key reused with different payload", HttpStatus.CONFLICT),
    // Native editor errors — blocks
    BLOCK_LIMIT_EXCEEDED("DOCUMENT_BLOCK_LIMIT_EXCEEDED", "Document exceeds maximum block count", HttpStatus.UNPROCESSABLE_ENTITY),
    BLOCK_DEPTH_EXCEEDED("DOCUMENT_BLOCK_DEPTH_EXCEEDED", "Document block nesting exceeds maximum depth", HttpStatus.UNPROCESSABLE_ENTITY),
    DUPLICATE_BLOCK_ID("DOCUMENT_DUPLICATE_BLOCK_ID", "Duplicate block ID found in document", HttpStatus.UNPROCESSABLE_ENTITY),
    // Native editor errors — governance
    DOCUMENT_LOCKED_FOR_EDIT("DOCUMENT_LOCKED", "Document is locked and cannot be edited", HttpStatus.LOCKED),
    DOCUMENT_FINALIZED("DOCUMENT_FINALIZED", "Document is finalized and cannot be edited", HttpStatus.CONFLICT),
    DOCUMENT_ARCHIVED("DOCUMENT_ARCHIVED", "Document is archived and cannot be edited", HttpStatus.CONFLICT),
    // Native editor errors — table
    TABLE_INVALID("DOCUMENT_TABLE_INVALID", "Simple table shape is invalid", HttpStatus.UNPROCESSABLE_ENTITY),
    TABLE_LIMIT_EXCEEDED("DOCUMENT_TABLE_LIMIT_EXCEEDED", "Simple table exceeds maximum rows or cells", HttpStatus.UNPROCESSABLE_ENTITY),
    // Native editor errors — sub-pages
    PARENT_CYCLE("DOCUMENT_PARENT_CYCLE", "Sub-page parent relationship would create a cycle", HttpStatus.UNPROCESSABLE_ENTITY),
    PARENT_SCOPE_MISMATCH("DOCUMENT_PARENT_SCOPE_MISMATCH", "Sub-page parent must belong to the same project", HttpStatus.UNPROCESSABLE_ENTITY),
    // Native editor errors — attachments
    ATTACHMENT_NOT_FOUND("DOCUMENT_ATTACHMENT_NOT_FOUND", "Document attachment not found", HttpStatus.NOT_FOUND),
    ATTACHMENT_NOT_AVAILABLE("DOCUMENT_ATTACHMENT_NOT_AVAILABLE", "Document attachment is not yet available", HttpStatus.CONFLICT),
    ATTACHMENT_SCOPE_MISMATCH("DOCUMENT_ATTACHMENT_SCOPE_MISMATCH", "Attachment does not belong to this document", HttpStatus.UNPROCESSABLE_ENTITY),
    // Native editor errors — mentions
    MENTION_TYPE_DISABLED("DOCUMENT_MENTION_TYPE_DISABLED", "This resource type is not enabled for mentions", HttpStatus.UNPROCESSABLE_ENTITY),
    MENTION_RESOURCE_NOT_FOUND("DOCUMENT_MENTION_RESOURCE_NOT_FOUND", "Mentioned resource does not exist", HttpStatus.UNPROCESSABLE_ENTITY),
    MENTION_DISCOVER_DENIED("DOCUMENT_MENTION_DISCOVER_DENIED", "You do not have permission to discover this resource", HttpStatus.FORBIDDEN),
    MENTION_READ_DENIED("DOCUMENT_MENTION_READ_DENIED", "You do not have permission to read this resource", HttpStatus.FORBIDDEN),
    MENTION_INSERT_DENIED("DOCUMENT_MENTION_INSERT_DENIED", "You do not have permission to mention this resource", HttpStatus.FORBIDDEN),
    MENTION_OUT_OF_SCOPE("DOCUMENT_MENTION_OUT_OF_SCOPE", "This resource is out of scope for mentions in this document", HttpStatus.UNPROCESSABLE_ENTITY),
    // Native editor errors — client visibility
    CLIENT_VISIBILITY_NOT_ALLOWED("DOCUMENT_CLIENT_VISIBILITY_INVALID", "Document contains issues that prevent client visibility", HttpStatus.CONFLICT),
    // Native editor errors — comments
    COMMENT_THREAD_NOT_FOUND("DOCUMENT_COMMENT_THREAD_NOT_FOUND", "Comment thread not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("DOCUMENT_COMMENT_NOT_FOUND", "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_ANCHOR_INVALID("DOCUMENT_COMMENT_ANCHOR_INVALID", "Comment anchor references a block that does not exist", HttpStatus.UNPROCESSABLE_ENTITY),
    COMMENT_VISIBILITY_DENIED("DOCUMENT_COMMENT_VISIBILITY_DENIED", "You do not have permission to use this comment visibility", HttpStatus.FORBIDDEN),
    // Native editor errors — suggestions
    SUGGESTION_NOT_FOUND("DOCUMENT_SUGGESTION_NOT_FOUND", "Suggestion not found", HttpStatus.NOT_FOUND),
    SUGGESTION_BASE_REVISION_CONFLICT("DOCUMENT_SUGGESTION_BASE_REVISION_CONFLICT", "Suggestion is based on a stale revision and cannot be applied", HttpStatus.CONFLICT),
    // Native editor errors — templates
    TEMPLATE_VARIABLE_MISSING("DOCUMENT_TEMPLATE_VARIABLE_MISSING", "Required template variable is missing", HttpStatus.UNPROCESSABLE_ENTITY),
    TEMPLATE_VARIABLE_DENIED("DOCUMENT_TEMPLATE_VARIABLE_DENIED", "Sensitive template variable cannot be rendered in this context", HttpStatus.FORBIDDEN),
    // Native editor errors — synced blocks
    SYNCED_BLOCK_NOT_FOUND("DOCUMENT_SYNCED_BLOCK_NOT_FOUND", "Synced block not found", HttpStatus.NOT_FOUND),
    SYNCED_BLOCK_CYCLE_DETECTED("SYNCED_BLOCK_RECURSION", "Synced block would create a recursive reference", HttpStatus.UNPROCESSABLE_ENTITY),
    // Governance
    BASELINE_GUARD_BLOCKED("GOVERNANCE_BASELINE_GUARD_BLOCKED", "Operation is blocked by baseline guard", HttpStatus.CONFLICT);

    private final String code; private final String defaultMessage; private final HttpStatus httpStatus;
    DocumentHubErrorCatalog(String c, String m, HttpStatus s) { code=c; defaultMessage=m; httpStatus=s; }
    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
