package com.company.scopery.modules.quality.defect.domain.model;
import com.company.scopery.modules.quality.defect.domain.enums.*;
import java.time.Instant; import java.util.UUID;
public record Defect(
        UUID id, UUID projectId, UUID workspaceId, String code, String title, String description,
        DefectCategory category, DefectSeverity severity, DefectPriority priority, DefectStatus status,
        UUID assignedToUserId, UUID reportedBy, Instant reportedAt, String reproductionSteps,
        String expectedResult, String actualResult, String environmentNotes,
        DefectResolutionType resolutionType, String resolutionNote, Instant resolvedAt, UUID resolvedBy,
        Instant closedAt, UUID closedBy, Instant reopenedAt, UUID reopenedBy, String reopenReason,
        UUID sourceTestCaseResultId, UUID sourceAiSuggestionId, Instant archivedAt, UUID archivedBy,
        String traceId, int version, Instant createdAt, Instant updatedAt
) {
    public static Defect create(UUID projectId, UUID workspaceId, String code, String title, String description,
                                DefectCategory category, DefectSeverity severity, DefectPriority priority,
                                UUID reportedBy, String reproductionSteps, String expectedResult, String actualResult,
                                UUID sourceTestCaseResultId) {
        Instant now = Instant.now();
        return new Defect(UUID.randomUUID(), projectId, workspaceId, code, title, description, category, severity, priority,
                DefectStatus.OPEN, null, reportedBy, now, reproductionSteps, expectedResult, actualResult, null,
                null, null, null, null, null, null, null, null, null, sourceTestCaseResultId, null, null, null, null, 0, now, now);
    }
    public Defect triage() { return withStatus(DefectStatus.TRIAGED); }
    public Defect assign(UUID userId) {
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority,
                DefectStatus.ASSIGNED, userId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult,
                environmentNotes, resolutionType, resolutionNote, resolvedAt, resolvedBy, closedAt, closedBy,
                reopenedAt, reopenedBy, reopenReason, sourceTestCaseResultId, sourceAiSuggestionId, archivedAt, archivedBy,
                traceId, version, createdAt, Instant.now());
    }
    public Defect markFixed(UUID actorId) {
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority,
                DefectStatus.FIXED, assignedToUserId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult,
                environmentNotes, resolutionType, resolutionNote, Instant.now(), actorId, closedAt, closedBy,
                reopenedAt, reopenedBy, reopenReason, sourceTestCaseResultId, sourceAiSuggestionId, archivedAt, archivedBy,
                traceId, version, createdAt, Instant.now());
    }
    public Defect readyForRetest() { return withStatus(DefectStatus.READY_FOR_RETEST); }
    public Defect verify() { return withStatus(DefectStatus.VERIFIED); }
    public Defect close(UUID actorId, DefectResolutionType type, String note) {
        if (type == null || note == null || note.isBlank()) throw new IllegalArgumentException("resolution required");
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority,
                DefectStatus.CLOSED, assignedToUserId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult,
                environmentNotes, type, note, resolvedAt != null ? resolvedAt : Instant.now(), resolvedBy != null ? resolvedBy : actorId,
                Instant.now(), actorId, reopenedAt, reopenedBy, reopenReason, sourceTestCaseResultId, sourceAiSuggestionId,
                archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public Defect reopen(UUID actorId, String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("reopen reason required");
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority,
                DefectStatus.REOPENED, assignedToUserId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult,
                environmentNotes, null, null, null, null, null, null, Instant.now(), actorId, reason,
                sourceTestCaseResultId, sourceAiSuggestionId, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public Defect archive(UUID actorId) {
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority,
                DefectStatus.ARCHIVED, assignedToUserId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult,
                environmentNotes, resolutionType, resolutionNote, resolvedAt, resolvedBy, closedAt, closedBy,
                reopenedAt, reopenedBy, reopenReason, sourceTestCaseResultId, sourceAiSuggestionId, Instant.now(), actorId,
                traceId, version, createdAt, Instant.now());
    }
    public Defect update(String title, String description, DefectCategory category, DefectSeverity severity, DefectPriority priority,
                         String reproductionSteps, String expectedResult, String actualResult, String environmentNotes) {
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority, status,
                assignedToUserId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult, environmentNotes,
                resolutionType, resolutionNote, resolvedAt, resolvedBy, closedAt, closedBy, reopenedAt, reopenedBy, reopenReason,
                sourceTestCaseResultId, sourceAiSuggestionId, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
    public boolean isOpenBlocker() {
        return (severity == DefectSeverity.BLOCKER || severity == DefectSeverity.CRITICAL)
                && status != DefectStatus.CLOSED && status != DefectStatus.REJECTED && status != DefectStatus.ARCHIVED
                && status != DefectStatus.VERIFIED;
    }
    private Defect withStatus(DefectStatus s) {
        return new Defect(id, projectId, workspaceId, code, title, description, category, severity, priority, s,
                assignedToUserId, reportedBy, reportedAt, reproductionSteps, expectedResult, actualResult, environmentNotes,
                resolutionType, resolutionNote, resolvedAt, resolvedBy, closedAt, closedBy, reopenedAt, reopenedBy, reopenReason,
                sourceTestCaseResultId, sourceAiSuggestionId, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
}
