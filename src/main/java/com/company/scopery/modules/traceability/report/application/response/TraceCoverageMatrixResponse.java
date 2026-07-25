package com.company.scopery.modules.traceability.report.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TraceCoverageMatrixResponse(
        Summary summary,
        List<Row> items,
        PageInfo page
) {
    public record Summary(
            long requirements,
            long covered,
            int coveredPct,
            long missingTests,
            long failed,
            long blocked,
            long notEvaluated,
            long atRisk
    ) {
        public static Summary of(long requirements, long covered, long missingTests,
                                  long notEvaluated, long atRisk, long failed, long blocked) {
            int pct = requirements == 0 ? 0 : (int) Math.round(covered * 100.0 / requirements);
            return new Summary(requirements, covered, pct, missingTests, failed, blocked, notEvaluated, atRisk);
        }
    }

    public record Row(
            UUID requirementId,
            String code,
            String title,
            String requirementType,
            String priority,
            UUID applicationId,
            UUID functionalItemId,

            String coverageStatus,
            long testCaseCount,
            long executedCount,
            long passedCount,
            long failedCount,
            long blockedCount,
            long notRunCount,

            String latestResult,
            Instant latestResultAt,
            UUID latestTestRunId,
            UUID latestTestCaseId,
            String latestTestCaseCode,
            String latestTestCaseTitle,

            long openDefectCount
    ) {}

    public record PageInfo(int limit, int offset, long total) {}
}
