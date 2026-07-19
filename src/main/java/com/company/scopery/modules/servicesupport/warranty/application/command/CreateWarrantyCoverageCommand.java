package com.company.scopery.modules.servicesupport.warranty.application.command;
import java.time.LocalDate; import java.util.UUID;
public record CreateWarrantyCoverageCommand(UUID projectId, UUID serviceProfileId, LocalDate startDate, LocalDate endDate) {}
