package com.company.scopery.modules.servicesupport.maintenance.http.request;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant; import java.util.UUID;
public record CreateMaintenancePlanRequest(@NotBlank String name, UUID projectId, Instant plannedStart, Instant plannedEnd) {}
