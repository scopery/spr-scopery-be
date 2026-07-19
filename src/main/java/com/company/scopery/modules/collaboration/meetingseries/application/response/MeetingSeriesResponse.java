package com.company.scopery.modules.collaboration.meetingseries.application.response;
import com.company.scopery.modules.collaboration.meetingseries.domain.model.MeetingSeries;
import java.time.Instant; import java.util.UUID;
public record MeetingSeriesResponse(UUID id, UUID workspaceId, UUID projectId, String code, String title, String description,
        String cadence, UUID ownerUserId, String status, boolean clientVisible, Instant archivedAt, Instant createdAt, Instant updatedAt, int version) {
    public static MeetingSeriesResponse from(MeetingSeries s) {
        return new MeetingSeriesResponse(s.id(), s.workspaceId(), s.projectId(), s.code(), s.title(), s.description(),
                s.cadence()==null?null:s.cadence().name(), s.ownerUserId(), s.status().name(), s.clientVisible(),
                s.archivedAt(), s.createdAt(), s.updatedAt(), s.version());
    }
}
