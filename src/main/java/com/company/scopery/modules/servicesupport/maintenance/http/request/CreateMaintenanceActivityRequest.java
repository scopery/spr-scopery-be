package com.company.scopery.modules.servicesupport.maintenance.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateMaintenanceActivityRequest(@NotNull UUID maintenancePlanId, @NotBlank String activityType, @NotBlank String title) {}
