package com.company.scopery.modules.servicesupport.maintenance.application.command;
import java.util.UUID;
public record CreateMaintenanceActivityCommand(UUID maintenancePlanId, String activityType, String title) {}
