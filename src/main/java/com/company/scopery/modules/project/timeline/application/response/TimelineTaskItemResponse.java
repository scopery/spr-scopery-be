package com.company.scopery.modules.project.timeline.application.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TimelineTaskItemResponse(
        UUID taskId,
        Integer estimateMinutes,
        BigDecimal progressPercent,
        LocalDate startDate,
        LocalDate endDate,
        List<TimelineBucketResponse> buckets
) {}
