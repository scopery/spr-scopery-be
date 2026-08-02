package com.company.scopery.modules.project.timeline.application.query;

import com.company.scopery.modules.project.timeline.domain.enums.TimelineGranularity;

import java.time.LocalDate;
import java.util.UUID;

public record TimelineViewQuery(
        UUID projectId,
        LocalDate from,
        LocalDate to,
        TimelineGranularity granularity
) {}
