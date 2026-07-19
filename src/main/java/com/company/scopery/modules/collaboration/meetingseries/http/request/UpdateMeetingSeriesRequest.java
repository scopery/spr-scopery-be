package com.company.scopery.modules.collaboration.meetingseries.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record UpdateMeetingSeriesRequest(@NotBlank String title, String description, String cadence, UUID ownerUserId, Boolean clientVisible) {}
