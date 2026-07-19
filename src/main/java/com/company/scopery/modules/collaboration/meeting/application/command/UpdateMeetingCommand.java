package com.company.scopery.modules.collaboration.meeting.application.command;
import java.time.Instant; import java.util.UUID;
public record UpdateMeetingCommand(
        UUID projectId, UUID meetingId, String title, String description, String meetingType,
        Instant startAt, Instant endAt, String timezone, String location, String meetingUrl, Boolean clientVisible
) {}
