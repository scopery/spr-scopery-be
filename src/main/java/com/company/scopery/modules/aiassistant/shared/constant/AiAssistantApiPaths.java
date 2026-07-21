package com.company.scopery.modules.aiassistant.shared.constant;

public final class AiAssistantApiPaths {

    public static final String BASE                = "/api/v1/ai-assistant";
    public static final String CONVERSATIONS       = BASE + "/conversations";
    public static final String CONVERSATION_BY_ID  = CONVERSATIONS + "/{id}";
    public static final String MESSAGES_BY_CONV    = CONVERSATIONS + "/{conversationId}/messages";
    public static final String MESSAGES            = BASE + "/messages";
    public static final String MESSAGE_BY_ID       = MESSAGES + "/{messageId}";
    public static final String GUIDES              = BASE + "/guides";
    public static final String FEEDBACKS           = BASE + "/feedbacks";
    public static final String WORKSPACE_CONFIG    = BASE + "/workspace-config";
    public static final String ADMIN_WORKSPACE_CONFIG         = "/api/v1/admin/ai-assistant/workspace-configs";
    public static final String ADMIN_WORKSPACE_CONFIG_BY_ID   = "/api/v1/admin/ai-assistant/workspace-configs/{workspaceId}";

    private AiAssistantApiPaths() {}
}
