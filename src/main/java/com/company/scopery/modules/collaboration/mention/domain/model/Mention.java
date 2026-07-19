package com.company.scopery.modules.collaboration.mention.domain.model;
import com.company.scopery.modules.collaboration.mention.domain.enums.MentionSourceType;
import com.company.scopery.modules.collaboration.mention.domain.enums.MentionTargetType;
import java.time.Instant; import java.util.UUID;
public record Mention(
        UUID id, UUID workspaceId, UUID projectId, MentionSourceType sourceType, UUID sourceId,
        MentionTargetType targetType, UUID targetId, boolean notificationSent,
        int version, Instant createdAt, Instant updatedAt
) {
    public static Mention create(UUID workspaceId, UUID projectId, MentionSourceType sourceType, UUID sourceId,
                                 MentionTargetType targetType, UUID targetId) {
        Instant now = Instant.now();
        return new Mention(UUID.randomUUID(), workspaceId, projectId, sourceType, sourceId, targetType, targetId, false, 0, now, now);
    }
}
