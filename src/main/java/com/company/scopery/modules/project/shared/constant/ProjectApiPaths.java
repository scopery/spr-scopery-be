package com.company.scopery.modules.project.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ProjectApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/projects";

    public static final String PROJECTS           = BASE;
    public static final String PHASE_DEFINITIONS  = ApiPaths.BASE_PATH + "/phase-definitions";
    public static final String PROJECT_PHASES     = BASE + "/{projectId}/phases";
    public static final String WBS_NODES          = BASE + "/{projectId}/wbs-nodes";
    public static final String TASKS              = BASE + "/{projectId}/tasks";
    public static final String TASK_DEPENDENCIES  = BASE + "/{projectId}/task-dependencies";

    private ProjectApiPaths() {}
}
