package com.company.scopery.modules.project.scheduling.schedulingissue.application.response;

import com.company.scopery.modules.project.scheduling.schedulingissue.domain.model.SchedulingIssue;
import java.time.LocalDate;
import java.util.UUID;

public record SchedulingIssueResponse(UUID id,UUID taskId,UUID userId,String issueType,String severity,
        String message,LocalDate issueDate) {
    public static SchedulingIssueResponse from(SchedulingIssue i){return new SchedulingIssueResponse(i.id(),i.taskId(),i.userId(),i.issueType().name(),i.severity().name(),i.message(),i.issueDate());}
}
