package com.company.scopery.modules.trust.datasubject.domain.model;
import java.time.Instant; import java.util.UUID;
public record DataSubjectIndex(UUID id, UUID workspaceId, String subjectType, UUID subjectId,
        String displayNameSnapshot, UUID linkedUserId, UUID linkedExternalContactId,
        long recordCount, Instant lastRebuiltAt, String status,
        int version, Instant createdAt, Instant updatedAt) {
    public static DataSubjectIndex create(UUID workspaceId, String subjectType, UUID subjectId,
            String displayNameSnapshot, UUID linkedUserId, UUID linkedExternalContactId) {
        Instant now = Instant.now();
        return new DataSubjectIndex(UUID.randomUUID(), workspaceId, subjectType, subjectId,
                displayNameSnapshot, linkedUserId, linkedExternalContactId, 0L, null, "ACTIVE", 0, now, now);
    }
    public DataSubjectIndex markRebuilt(long count) {
        return new DataSubjectIndex(id, workspaceId, subjectType, subjectId, displayNameSnapshot,
                linkedUserId, linkedExternalContactId, count, Instant.now(), status, version, createdAt, Instant.now());
    }
}
