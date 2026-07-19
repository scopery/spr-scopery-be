package com.company.scopery.modules.documenthub.shared.constant;
public final class DocumentHubActivityActions {
    public static final String FOLDER_CREATED = "DOCUMENT_FOLDER_CREATED";
    public static final String DOCUMENT_CREATED = "DOCUMENT_CREATED";
    public static final String VERSION_UPLOADED = "DOCUMENT_VERSION_UPLOADED";
    public static final String DOCUMENT_APPROVED = "DOCUMENT_APPROVED";
    public static final String TEMPLATE_CREATED = "DOCUMENT_TEMPLATE_CREATED";
    public static final String GENERATION_REQUESTED = "DOCUMENT_GENERATION_REQUESTED";
    public static final String GENERATION_COMPLETED = "DOCUMENT_GENERATION_COMPLETED";
    // Phase 41 — object storage
    public static final String VERSION_PRESIGNED_UPLOAD_CREATED = "DOCUMENT_VERSION_PRESIGNED_UPLOAD_CREATED";
    public static final String VERSION_UPLOAD_COMPLETED = "DOCUMENT_VERSION_UPLOAD_COMPLETED";
    public static final String VERSION_PRESIGNED_DOWNLOAD_CREATED = "DOCUMENT_VERSION_PRESIGNED_DOWNLOAD_CREATED";
    // Native editor actions
    public static final String CONTENT_SAVED = "DOCUMENT_CONTENT_SAVED";
    public static final String CONTENT_NOOP_DETECTED = "DOCUMENT_CONTENT_NOOP_DETECTED";
    public static final String REVISION_RESTORED = "DOCUMENT_REVISION_RESTORED";
    public static final String REVISION_CREATED = "DOCUMENT_REVISION_CREATED";
    public static final String CONTENT_MODE_CHANGED = "DOCUMENT_CONTENT_MODE_CHANGED";
    public static final String COMMENT_THREAD_OPENED = "DOCUMENT_COMMENT_THREAD_OPENED";
    public static final String COMMENT_THREAD_RESOLVED = "DOCUMENT_COMMENT_THREAD_RESOLVED";
    public static final String COMMENT_ADDED = "DOCUMENT_COMMENT_ADDED";
    public static final String COMMENT_DELETED = "DOCUMENT_COMMENT_DELETED";
    public static final String SUGGESTION_CREATED = "DOCUMENT_SUGGESTION_CREATED";
    public static final String SUGGESTION_ACCEPTED = "DOCUMENT_SUGGESTION_ACCEPTED";
    public static final String SUGGESTION_REJECTED = "DOCUMENT_SUGGESTION_REJECTED";
    public static final String ATTACHMENT_PRESIGNED_UPLOAD_CREATED = "DOCUMENT_ATTACHMENT_PRESIGNED_UPLOAD_CREATED";
    public static final String ATTACHMENT_UPLOAD_COMPLETED = "DOCUMENT_ATTACHMENT_UPLOAD_COMPLETED";
    public static final String SYNCED_BLOCK_CREATED = "SYNCED_BLOCK_CREATED";
    public static final String SYNCED_BLOCK_UPDATED = "SYNCED_BLOCK_UPDATED";
    public static final String SYNCED_BLOCK_ARCHIVED = "SYNCED_BLOCK_ARCHIVED";
    public static final String CLIENT_VISIBILITY_VALIDATED = "DOCUMENT_CLIENT_VISIBILITY_VALIDATED";
    private DocumentHubActivityActions() {}
}
