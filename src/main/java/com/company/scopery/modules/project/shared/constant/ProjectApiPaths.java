package com.company.scopery.modules.project.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ProjectApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/projects";

    public static final String PROJECTS           = BASE;
    /**
     * Phase definitions remain on legacy path {@code /api/phase-definitions} for client compatibility.
     * Spec §10.1 suggested {@code /api/project/phase-definitions}; we keep the existing path.
     */
    public static final String PHASE_DEFINITIONS  = ApiPaths.BASE_PATH + "/phase-definitions";
    public static final String PROJECT_PHASES     = BASE + "/{projectId}/phases";
    public static final String WBS_NODES          = BASE + "/{projectId}/wbs-nodes";
    public static final String TASKS              = BASE + "/{projectId}/tasks";
    public static final String TASK_DEPENDENCIES  = BASE + "/{projectId}/task-dependencies";
    public static final String SCHEDULE_RUNS = BASE + "/{projectId}/schedule-runs";
    public static final String CURRENT_SCHEDULE = BASE + "/{projectId}/schedule/current";
    public static final String TASK_SCHEDULE = BASE + "/{projectId}/tasks/{taskId}/schedule";

    // Phase 14 — Gantt
    public static final String GANTT = BASE + "/{projectId}/gantt";
    public static final String GANTT_CRITICAL_PATH = GANTT + "/critical-path";
    public static final String GANTT_EXPORT = GANTT + "/export";
    public static final String GANTT_ITEMS = GANTT + "/items";
    public static final String GANTT_DEPENDENCIES = GANTT + "/dependencies";
    public static final String GANTT_ISSUES = GANTT + "/issues";
    public static final String GANTT_RECALCULATE = GANTT + "/recalculate";
    public static final String GANTT_TASK_MOVE = GANTT + "/tasks/{taskId}/move";
    public static final String GANTT_TASK_RESIZE = GANTT + "/tasks/{taskId}/resize";
    public static final String GANTT_TASK_CLEAR_OVERRIDE = GANTT + "/tasks/{taskId}/clear-override";
    public static final String MILESTONES = BASE + "/{projectId}/milestones";

    // Phase 11 — templates under /api/project/templates
    private static final String TEMPLATE_BASE = ApiPaths.BASE_PATH + "/project/templates";

    public static final String PROJECT_TEMPLATES = TEMPLATE_BASE;
    public static final String PROJECT_TEMPLATE_VERSIONS =
            TEMPLATE_BASE + "/{templateId}/versions";
    public static final String PROJECT_TEMPLATE_PHASES =
            TEMPLATE_BASE + "/{templateId}/versions/{versionId}/phases";
    public static final String PROJECT_TEMPLATE_WBS_NODES =
            TEMPLATE_BASE + "/{templateId}/versions/{versionId}/wbs-nodes";
    public static final String PROJECT_TEMPLATE_WBS_TREE =
            TEMPLATE_BASE + "/{templateId}/versions/{versionId}/wbs-tree";
    public static final String PROJECT_TEMPLATE_TASKS =
            TEMPLATE_BASE + "/{templateId}/versions/{versionId}/tasks";
    public static final String PROJECT_TEMPLATE_TASK_DEPENDENCIES =
            TEMPLATE_BASE + "/{templateId}/versions/{versionId}/task-dependencies";

    private ProjectApiPaths() {}
}
