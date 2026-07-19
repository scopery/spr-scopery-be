package com.company.scopery.modules.project.scheduling.schedulingissue.domain.enums;

public enum SchedulingIssueType {
    TASK_NO_ASSIGNEE, TASK_NO_ESTIMATE, TASK_INVALID_ESTIMATE, TASK_NO_CAPACITY,
    TASK_DUE_DATE_CAPACITY_GAP, TASK_DEPENDENCY_CYCLE, TASK_DEPENDENCY_UNSCHEDULED,
    RESOURCE_OVER_ALLOCATED, CALENDAR_MISSING, ALLOCATION_MISSING,
    UNSUPPORTED_DEPENDENCY_TYPE, PLANNING_HORIZON_EXCEEDED,
    /** Phase 14: active TaskScheduleOverride influenced this task's placement. */
    MANUAL_SCHEDULE_OVERRIDE_APPLIED
}
