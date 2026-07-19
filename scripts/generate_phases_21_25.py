#!/usr/bin/env python3
"""Generate Phase 21-25 modules for Scopery backend."""
from pathlib import Path

ROOT = Path("/Users/nguyenngocynhi/Desktop/My Workspace/2026/Scopery V2/spr-scopery-be")
JAVA = ROOT / "src/main/java/com/company/scopery"
TEST = ROOT / "src/test/java/com/company/scopery"
MIG = ROOT / "src/main/resources/db/migration"
DOCS = ROOT / "src/docs/phase-complete"

def write(rel: str, content: str):
    path = ROOT / rel if not rel.startswith("src/") else ROOT / rel
    # Allow absolute-style relative from ROOT
    if rel.startswith("src/"):
        path = ROOT / rel
    else:
        path = Path(rel)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")
    print(f"W {path.relative_to(ROOT)}")

def j(pkg_path: str, name: str, content: str):
    """Write java under modules/..."""
    write(f"src/main/java/com/company/scopery/{pkg_path}/{name}.java", content)

# ============================================================
# PHASE 21 — aiplanning
# ============================================================

# Shared constants
j("modules/aiplanning/shared/constant", "AiPlanningModuleCodes", """
package com.company.scopery.modules.aiplanning.shared.constant;

public final class AiPlanningModuleCodes {
    public static final String AI_PLANNING = "AI_PLANNING";
    private AiPlanningModuleCodes() {}
}
""")

j("modules/aiplanning/shared/constant", "AiPlanningTableNames", """
package com.company.scopery.modules.aiplanning.shared.constant;

public final class AiPlanningTableNames {
    public static final String PLANNING_RUN = "ai_planning_run";
    public static final String CONTEXT_SNAPSHOT = "ai_planning_context_snapshot";
    public static final String SUGGESTION = "ai_planning_suggestion";
    public static final String SUGGESTION_ITEM = "ai_planning_suggestion_item";
    public static final String REVIEW_ACTION = "ai_planning_review_action";
    public static final String APPLY_RESULT = "ai_planning_apply_result";
    private AiPlanningTableNames() {}
}
""")

j("modules/aiplanning/shared/constant", "AiPlanningApiPaths", """
package com.company.scopery.modules.aiplanning.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class AiPlanningApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}/ai-planning";
    public static final String RUNS = BASE + "/runs";
    public static final String SUGGESTIONS = BASE + "/suggestions";
    private AiPlanningApiPaths() {}
}
""")

j("modules/aiplanning/shared/constant", "AiPlanningEntityTypes", """
package com.company.scopery.modules.aiplanning.shared.constant;

public final class AiPlanningEntityTypes {
    public static final String PLANNING_RUN = "AI_PLANNING_RUN";
    public static final String CONTEXT_SNAPSHOT = "AI_PLANNING_CONTEXT_SNAPSHOT";
    public static final String SUGGESTION = "AI_PLANNING_SUGGESTION";
    public static final String SUGGESTION_ITEM = "AI_PLANNING_SUGGESTION_ITEM";
    public static final String REVIEW_ACTION = "AI_PLANNING_REVIEW_ACTION";
    public static final String APPLY_RESULT = "AI_PLANNING_APPLY_RESULT";
    private AiPlanningEntityTypes() {}
}
""")

j("modules/aiplanning/shared/constant", "AiPlanningActivityActions", """
package com.company.scopery.modules.aiplanning.shared.constant;

public final class AiPlanningActivityActions {
    public static final String RUN_CREATED = "AI_PLANNING_RUN_CREATED";
    public static final String RUN_COMPLETED = "AI_PLANNING_RUN_COMPLETED";
    public static final String RUN_FAILED = "AI_PLANNING_RUN_FAILED";
    public static final String RUN_CANCELLED = "AI_PLANNING_RUN_CANCELLED";
    public static final String SUGGESTION_GENERATED = "AI_PLANNING_SUGGESTION_GENERATED";
    public static final String SUGGESTION_REVIEW_STARTED = "AI_PLANNING_SUGGESTION_REVIEW_STARTED";
    public static final String SUGGESTION_ACCEPTED = "AI_PLANNING_SUGGESTION_ACCEPTED";
    public static final String SUGGESTION_REJECTED = "AI_PLANNING_SUGGESTION_REJECTED";
    public static final String SUGGESTION_ARCHIVED = "AI_PLANNING_SUGGESTION_ARCHIVED";
    public static final String SUGGESTION_APPLIED = "AI_PLANNING_SUGGESTION_APPLIED";
    public static final String ITEM_ACCEPTED = "AI_PLANNING_ITEM_ACCEPTED";
    public static final String ITEM_REJECTED = "AI_PLANNING_ITEM_REJECTED";
    public static final String ITEM_APPLIED = "AI_PLANNING_ITEM_APPLIED";
    private AiPlanningActivityActions() {}
}
""")

j("modules/aiplanning/shared/constant", "AiPlanningSortFields", """
package com.company.scopery.modules.aiplanning.shared.constant;

public final class AiPlanningSortFields {
    public static final String CREATED_AT = "createdAt";
    public static final String STATUS = "status";
    private AiPlanningSortFields() {}
}
""")

j("modules/aiplanning/shared/constant", "AiPlanningPromptCodes", """
package com.company.scopery.modules.aiplanning.shared.constant;

import java.util.List;

public final class AiPlanningPromptCodes {
    public static final String PROJECT_PLAN_DRAFT_PROMPT = "PROJECT_PLAN_DRAFT_PROMPT";
    public static final String PROJECT_TEMPLATE_RECOMMENDATION_PROMPT = "PROJECT_TEMPLATE_RECOMMENDATION_PROMPT";
    public static final String TASK_ESTIMATE_SUGGESTION_PROMPT = "TASK_ESTIMATE_SUGGESTION_PROMPT";
    public static final String COST_ROLE_SUGGESTION_PROMPT = "COST_ROLE_SUGGESTION_PROMPT";
    public static final String SCHEDULE_RISK_EXPLANATION_PROMPT = "SCHEDULE_RISK_EXPLANATION_PROMPT";
    public static final String FINANCE_INSIGHT_PROMPT = "FINANCE_INSIGHT_PROMPT";
    public static final String QUOTE_PROPOSAL_DRAFT_PROMPT = "QUOTE_PROPOSAL_DRAFT_PROMPT";
    public static final String CHANGE_REQUEST_DRAFT_PROMPT = "CHANGE_REQUEST_DRAFT_PROMPT";

    public static List<String> all() {
        return List.of(
                PROJECT_PLAN_DRAFT_PROMPT,
                PROJECT_TEMPLATE_RECOMMENDATION_PROMPT,
                TASK_ESTIMATE_SUGGESTION_PROMPT,
                COST_ROLE_SUGGESTION_PROMPT,
                SCHEDULE_RISK_EXPLANATION_PROMPT,
                FINANCE_INSIGHT_PROMPT,
                QUOTE_PROPOSAL_DRAFT_PROMPT,
                CHANGE_REQUEST_DRAFT_PROMPT);
    }

    private AiPlanningPromptCodes() {}
}
""")

print("Phase 21 shared constants done")
