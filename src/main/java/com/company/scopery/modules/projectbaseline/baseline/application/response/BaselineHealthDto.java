package com.company.scopery.modules.projectbaseline.baseline.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BaselineHealthDto(
        String snapshotStatus,
        List<SourceCheckDto> sources,
        ApprovalInfoDto approval,
        List<IssueDto> issues
) {
    public record SourceCheckDto(String source, String status) {}

    public record ApprovalInfoDto(String status, Instant approvedAt, UUID approvedBy) {}

    public record IssueDto(String code, String message, String severity) {}
}
