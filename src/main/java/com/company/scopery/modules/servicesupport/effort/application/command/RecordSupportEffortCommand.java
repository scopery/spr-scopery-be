package com.company.scopery.modules.servicesupport.effort.application.command;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RecordSupportEffortCommand(UUID caseId, UUID resourceProfileId, BigDecimal effortHours, LocalDate effortDate) {}
