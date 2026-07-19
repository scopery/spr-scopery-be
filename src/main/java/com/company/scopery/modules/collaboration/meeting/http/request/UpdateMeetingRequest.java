package com.company.scopery.modules.collaboration.meeting.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.time.Instant;
public record UpdateMeetingRequest(
        @NotBlank String title,
        String description,
        @NotBlank String meetingType,
        @NotNull Instant startAt,
        Instant endAt,
        String timezone,
        String location,
        String meetingUrl,
        Boolean clientVisible
) {}
