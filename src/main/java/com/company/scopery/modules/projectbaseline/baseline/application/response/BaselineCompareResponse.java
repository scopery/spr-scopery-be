package com.company.scopery.modules.projectbaseline.baseline.application.response;

import java.util.List;

public record BaselineCompareResponse(
        SideDto left, SideDto right,
        List<DeltaItemDto> deltas,
        ChangeCountsDto changeCounts,
        List<String> highlights
) {
    public record SideDto(String label, BaselineSummaryDto summary) {}

    public record DeltaItemDto(String field, String label, Object baseline, Object current, String direction) {}

    public record ChangeCountsDto(int phasesAdded, int phasesRemoved, int wbsAdded, int wbsRemoved,
                                  int tasksAdded, int tasksRemoved, int milestonesAdded, int milestonesRemoved) {}
}
