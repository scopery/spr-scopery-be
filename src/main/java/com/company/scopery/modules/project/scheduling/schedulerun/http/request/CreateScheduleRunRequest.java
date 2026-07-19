package com.company.scopery.modules.project.scheduling.schedulerun.http.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateScheduleRunRequest(@NotNull LocalDate planningStartDate,@NotNull LocalDate planningEndDate,
                                       Options options) {
    public record Options(Boolean includeCompletedTasks,Boolean useProjectAllocationsOnly,Boolean markAsCurrent) {}
    public boolean includeCompletedTasks(){return options!=null&&Boolean.TRUE.equals(options.includeCompletedTasks());}
    public boolean markAsCurrent(){return options==null||!Boolean.FALSE.equals(options.markAsCurrent());}
}
