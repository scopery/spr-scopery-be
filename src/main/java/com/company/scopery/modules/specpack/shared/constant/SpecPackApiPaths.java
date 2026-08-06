package com.company.scopery.modules.specpack.shared.constant;

public final class SpecPackApiPaths {
    private static final String BASE = "/api/v1/projects/{projectId}";

    public static final String SPEC_PACKS              = BASE + "/spec-packs";
    public static final String SPEC_PACK_BY_ID         = SPEC_PACKS + "/{packId}";
    public static final String SPEC_PACK_RESTORE       = SPEC_PACK_BY_ID + "/restore";

    public static final String BLOCKS                  = SPEC_PACK_BY_ID + "/blocks";
    public static final String BLOCK_BY_ID             = BLOCKS + "/{blockId}";
    public static final String BLOCK_REORDER           = BLOCKS + "/reorder";
    public static final String BLOCK_DUPLICATE         = BLOCK_BY_ID + "/duplicate";
    public static final String BLOCK_REVISIONS         = BLOCK_BY_ID + "/revisions";
    public static final String BLOCK_RESTORE_REVISION  = BLOCK_BY_ID + "/restore-revision";

    public static final String IMPORTS_VALIDATE_JSON   = SPEC_PACK_BY_ID + "/imports/validate-json";
    public static final String IMPORTS_PREVIEW_JSON    = SPEC_PACK_BY_ID + "/imports/preview-json";
    public static final String IMPORTS_APPLY_JSON      = SPEC_PACK_BY_ID + "/imports/apply-json";
    public static final String IMPORTS_PREVIEW_ZIP     = SPEC_PACKS + "/imports/preview-zip";
    public static final String IMPORTS_APPLY_ZIP       = SPEC_PACKS + "/imports/apply-zip";

    public static final String ASSETS                  = SPEC_PACK_BY_ID + "/assets";
    public static final String ASSET_BY_ID             = ASSETS + "/{assetId}";
    public static final String ASSET_DOWNLOAD          = ASSET_BY_ID + "/download";
    public static final String ASSET_REVISIONS         = ASSET_BY_ID + "/revisions";

    public static final String VERSIONS                = SPEC_PACK_BY_ID + "/versions";
    public static final String VERSION_BY_ID           = VERSIONS + "/{versionId}";
    public static final String VERSION_COMPARE         = VERSION_BY_ID + "/compare/{otherVersionId}";
    public static final String VERSION_RESTORE         = VERSION_BY_ID + "/restore";

    public static final String AGENT_SESSIONS          = BASE + "/spec-pack-agent-sessions";
    public static final String AGENT_SESSION_BY_ID     = AGENT_SESSIONS + "/{sessionId}";
    public static final String AGENT_SESSION_CANCEL    = AGENT_SESSION_BY_ID + "/cancel";
    public static final String STAGE_COMPLETE          = AGENT_SESSION_BY_ID + "/stages/{stageCode}/complete";

    public static final String CLARIFICATIONS          = AGENT_SESSION_BY_ID + "/clarifications";
    public static final String CLARIFICATION_BY_ID     = CLARIFICATIONS + "/{clarificationId}";
    public static final String CLARIFICATIONS_IMPORT   = CLARIFICATIONS + "/import";

    public static final String READINESS               = AGENT_SESSION_BY_ID + "/readiness";

    public static final String OUTLINES                = AGENT_SESSION_BY_ID + "/outlines";
    public static final String OUTLINE_BY_ID           = OUTLINES + "/{outlineId}";
    public static final String OUTLINE_APPROVE         = OUTLINE_BY_ID + "/approve";

    public static final String PROMPTS                 = AGENT_SESSION_BY_ID + "/prompts";
    public static final String PROMPT_MASTER           = PROMPTS + "/master";
    public static final String PROMPT_BY_STAGE         = PROMPTS + "/{stageCode}";
    public static final String PROMPT_RENDER           = PROMPTS + "/render";

    private SpecPackApiPaths() {}
}
