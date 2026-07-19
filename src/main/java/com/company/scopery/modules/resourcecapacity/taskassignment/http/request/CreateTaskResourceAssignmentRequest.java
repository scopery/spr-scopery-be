package com.company.scopery.modules.resourcecapacity.taskassignment.http.request;
import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal; import java.util.UUID;
public record CreateTaskResourceAssignmentRequest(@NotNull UUID resourceProfileId, @NotBlank String assignmentType, BigDecimal plannedEffortHours) {}
