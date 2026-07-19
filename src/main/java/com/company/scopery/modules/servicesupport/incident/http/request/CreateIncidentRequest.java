package com.company.scopery.modules.servicesupport.incident.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateIncidentRequest(@NotBlank String title, @NotBlank String severity, UUID projectId) {}
