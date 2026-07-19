package com.company.scopery.modules.documenthub.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class DocumentHubApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String FOLDERS = BASE + "/document-folders";
    public static final String DOCUMENTS = BASE + "/documents";
    public static final String DOCUMENT_VERSIONS = DOCUMENTS + "/{documentId}/versions";
    public static final String DOCUMENT_SHARES = DOCUMENTS + "/{documentId}/shares";
    public static final String TEMPLATES = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/document-templates";
    public static final String GENERATED_JOBS = BASE + "/generated-documents";
    public static final String REPORTS = BASE + "/reports";
    // Native editor paths
    public static final String DOCUMENT_CONTENT = DOCUMENTS + "/{documentId}/content";
    public static final String DOCUMENT_REVISIONS = DOCUMENTS + "/{documentId}/revisions";
    public static final String DOCUMENT_COMMENT_THREADS = DOCUMENTS + "/{documentId}/comment-threads";
    public static final String DOCUMENT_SUGGESTIONS = DOCUMENTS + "/{documentId}/suggestions";
    public static final String DOCUMENT_ATTACHMENTS = DOCUMENTS + "/{documentId}/attachments";
    public static final String DOCUMENT_SUBPAGES = DOCUMENTS + "/{documentId}/subpages";
    public static final String DOCUMENT_VALIDATE_CLIENT_VISIBILITY = DOCUMENTS + "/{documentId}/validate-client-visibility";
    public static final String SYNCED_BLOCKS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/synced-blocks";
    public static final String DOCUMENT_COMMENT_THREADS_BY_ID = ApiPaths.BASE_PATH + "/document-comment-threads";
    public static final String DOCUMENT_COMMENTS_BY_ID = ApiPaths.BASE_PATH + "/document-comments";
    public static final String DOCUMENT_SUGGESTIONS_BY_ID = ApiPaths.BASE_PATH + "/document-suggestions";
    public static final String TEMPLATE_VERSIONS = TEMPLATES + "/{templateId}/versions";
    public static final String NATIVE_TEMPLATE_VERSIONS = TEMPLATES + "/{templateId}/native-versions";
    public static final String NATIVE_TEMPLATE_VERSION_INSTANTIATE = NATIVE_TEMPLATE_VERSIONS + "/{versionId}/instantiate";
    private DocumentHubApiPaths() {}
}
