package com.company.scopery.modules.knowledge.shared.constant;

public final class KnowledgeActivityActions {
    private KnowledgeActivityActions() {}

    public static final String CREATE_DOCUMENT_TYPE = "CREATE_DOCUMENT_TYPE";
    public static final String UPDATE_DOCUMENT_TYPE = "UPDATE_DOCUMENT_TYPE";
    public static final String ACTIVATE_DOCUMENT_TYPE = "ACTIVATE_DOCUMENT_TYPE";
    public static final String DEACTIVATE_DOCUMENT_TYPE = "DEACTIVATE_DOCUMENT_TYPE";
    public static final String ARCHIVE_DOCUMENT_TYPE = "ARCHIVE_DOCUMENT_TYPE";
    /** @deprecated Prefer {@link #ARCHIVE_DOCUMENT_TYPE}. */
    @Deprecated
    public static final String SOFT_DELETE_DOCUMENT_TYPE = "SOFT_DELETE_DOCUMENT_TYPE";
    public static final String SEED_DOCUMENT_TYPE = "SEED_DOCUMENT_TYPE";

    public static final String CREATE_DOCUMENT_TYPE_FIELD = "CREATE_DOCUMENT_TYPE_FIELD";
    public static final String UPDATE_DOCUMENT_TYPE_FIELD = "UPDATE_DOCUMENT_TYPE_FIELD";
    public static final String ACTIVATE_DOCUMENT_TYPE_FIELD = "ACTIVATE_DOCUMENT_TYPE_FIELD";
    public static final String DEACTIVATE_DOCUMENT_TYPE_FIELD = "DEACTIVATE_DOCUMENT_TYPE_FIELD";
    public static final String ARCHIVE_DOCUMENT_TYPE_FIELD = "ARCHIVE_DOCUMENT_TYPE_FIELD";
    public static final String REORDER_DOCUMENT_TYPE_FIELDS = "REORDER_DOCUMENT_TYPE_FIELDS";

    // Phase 41 — indexing + retrieval
    public static final String REINDEX_SOURCE = "REINDEX_SOURCE";
    public static final String REINDEX_PROJECT = "REINDEX_PROJECT";
    public static final String REINDEX_WORKSPACE = "REINDEX_WORKSPACE";
    public static final String INVALIDATE_SOURCE = "INVALIDATE_SOURCE";
    public static final String PRESIGNED_UPLOAD_CREATED = "PRESIGNED_UPLOAD_CREATED";
    public static final String UPLOAD_COMPLETED = "UPLOAD_COMPLETED";
    public static final String PRESIGNED_DOWNLOAD_CREATED = "PRESIGNED_DOWNLOAD_CREATED";
}
