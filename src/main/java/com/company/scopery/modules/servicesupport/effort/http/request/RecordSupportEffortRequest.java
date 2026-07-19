package com.company.scopery.modules.servicesupport.effort.http.request;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record RecordSupportEffortRequest(@NotNull UUID caseId, UUID resourceProfileId,
        @NotNull BigDecimal effortHours, LocalDate effortDate) {}
