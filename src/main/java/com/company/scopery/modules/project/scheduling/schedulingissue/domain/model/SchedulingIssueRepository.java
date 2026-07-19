package com.company.scopery.modules.project.scheduling.schedulingissue.domain.model;

import java.util.List;
import java.util.UUID;

public interface SchedulingIssueRepository {
    List<SchedulingIssue> saveAll(List<SchedulingIssue> issues);
    List<SchedulingIssue> findAllByScheduleRunId(UUID runId);
}
