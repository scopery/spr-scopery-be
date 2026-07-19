package com.company.scopery.modules.collaboration.meeting.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.time.Instant; import java.util.UUID;
public record CreateMeetingRequest(
        UUID meetingSeriesId,
        @NotBlank String title,
        String description,
        @NotBlank String meetingType,
        @NotNull Instant startAt,
        Instant endAt,
        String timezone,
        String location,
        String meetingUrl,
        UUID organizerUserId,
        Boolean clientVisible
) {}
