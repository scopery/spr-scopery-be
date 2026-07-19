package com.company.scopery.modules.servicesupport.maintenance.application.command;
import java.time.Instant; import java.util.UUID;
public record CreateMaintenanceWindowCommand(UUID maintenancePlanId, Instant scheduledStart, Instant scheduledEnd, String title) {}
