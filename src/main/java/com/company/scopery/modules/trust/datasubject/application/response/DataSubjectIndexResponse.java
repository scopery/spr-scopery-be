package com.company.scopery.modules.trust.datasubject.application.response;
import com.company.scopery.modules.trust.datasubject.domain.model.DataSubjectIndex;
import java.time.Instant; import java.util.UUID;
public record DataSubjectIndexResponse(UUID id, UUID workspaceId, String subjectType, UUID subjectId,
        String displayNameSnapshot, long recordCount, Instant lastRebuiltAt, String status, Instant createdAt) {
    public static DataSubjectIndexResponse from(DataSubjectIndex d) {
        return new DataSubjectIndexResponse(d.id(), d.workspaceId(), d.subjectType(), d.subjectId(),
                d.displayNameSnapshot(), d.recordCount(), d.lastRebuiltAt(), d.status(), d.createdAt());
    }
}
