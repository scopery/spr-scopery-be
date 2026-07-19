package com.company.scopery.modules.servicesupport.maintenance.application.command;
import java.time.Instant; import java.util.UUID;
public record CreateMaintenancePlanCommand(String name, UUID projectId, Instant plannedStart, Instant plannedEnd) {}
