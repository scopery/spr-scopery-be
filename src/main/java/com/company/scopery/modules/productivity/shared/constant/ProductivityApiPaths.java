package com.company.scopery.modules.productivity.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ProductivityApiPaths {
    public static final String SEARCH = ApiPaths.BASE_PATH + "/search";
    private static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}";
    public static final String SAVED_SEARCHES = WS + "/saved-searches";
    public static final String SAVED_VIEWS = WS + "/saved-views";
    public static final String FAVORITES = WS + "/favorites";
    public static final String PINS = WS + "/pins";
    public static final String RECENT = WS + "/recent-items";
    public static final String WORK_INBOX = WS + "/work-inbox";
    public static final String COMMANDS = WS + "/commands";
    public static final String NAVIGATION = WS + "/navigation";
    private ProductivityApiPaths(){}
}
