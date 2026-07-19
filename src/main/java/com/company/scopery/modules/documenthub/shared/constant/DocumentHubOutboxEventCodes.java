package com.company.scopery.modules.documenthub.shared.constant;

public final class DocumentHubOutboxEventCodes {
    public static final String DOCUMENT_CREATED = "DOCUMENT_CREATED";
    public static final String DOCUMENT_APPROVED = "DOCUMENT_APPROVED";
    public static final String DOCUMENT_VERSION_UPLOADED = "DOCUMENT_VERSION_UPLOADED";
    public static final String DOCUMENT_GENERATION_REQUESTED = "DOCUMENT_GENERATION_REQUESTED";
    public static final String DOCUMENT_GENERATION_COMPLETED = "DOCUMENT_GENERATION_COMPLETED";
    // Native editor event codes
    public static final String DOCUMENT_CONTENT_SAVED = "DOCUMENT_CONTENT_SAVED";
    public static final String DOCUMENT_MENTION_EXTRACTED = "DOCUMENT_MENTION_EXTRACTED";
    public static final String DOCUMENT_COMMENT_THREAD_OPENED = "DOCUMENT_COMMENT_THREAD_OPENED";
    public static final String DOCUMENT_COMMENT_THREAD_RESOLVED = "DOCUMENT_COMMENT_THREAD_RESOLVED";
    public static final String DOCUMENT_SUGGESTION_CREATED = "DOCUMENT_SUGGESTION_CREATED";
    public static final String DOCUMENT_SUGGESTION_ACCEPTED = "DOCUMENT_SUGGESTION_ACCEPTED";
    public static final String DOCUMENT_SUGGESTION_REJECTED = "DOCUMENT_SUGGESTION_REJECTED";
    public static final String SYNCED_BLOCK_UPDATED = "SYNCED_BLOCK_UPDATED";
    public static final String DOCUMENT_CLIENT_VISIBILITY_VALIDATED = "DOCUMENT_CLIENT_VISIBILITY_VALIDATED";

    private DocumentHubOutboxEventCodes() {}
}
