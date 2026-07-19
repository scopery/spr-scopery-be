package com.company.scopery.modules.servicesupport.sla.http.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateSlaTargetRequest(@NotNull UUID slaPolicyId, @NotBlank String targetType, int durationMinutes,
        UUID requestTypeId, String priority) {}
