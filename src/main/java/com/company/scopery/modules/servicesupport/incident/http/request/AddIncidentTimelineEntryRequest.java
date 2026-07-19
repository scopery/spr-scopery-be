package com.company.scopery.modules.servicesupport.incident.http.request;
import jakarta.validation.constraints.NotBlank;
public record AddIncidentTimelineEntryRequest(@NotBlank String entryType, @NotBlank String message, String visibility) {}
