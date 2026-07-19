package com.company.scopery.modules.collaboration.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class CollaborationApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}";
    public static final String MEETING_SERIES = BASE + "/meeting-series";
    public static final String MEETINGS = BASE + "/meetings";
    public static final String ACTION_ITEMS = BASE + "/meeting-action-items";
    public static final String COMMENT_THREADS = BASE + "/comments/threads";
    public static final String COMMENTS = BASE + "/comments";
    public static final String REPORTS = BASE + "/reports";
    private CollaborationApiPaths() {}
}
