package com.company.scopery.modules.servicesupport.maintenance.http.request;
import jakarta.validation.constraints.NotNull;
import java.time.Instant; import java.util.UUID;
public record CreateMaintenanceWindowRequest(@NotNull UUID maintenancePlanId, Instant scheduledStart, Instant scheduledEnd, String title) {}
