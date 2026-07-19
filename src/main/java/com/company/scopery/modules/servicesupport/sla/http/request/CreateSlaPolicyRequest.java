package com.company.scopery.modules.servicesupport.sla.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateSlaPolicyRequest(@NotBlank String policyCode, @NotBlank String name,
        Integer firstResponseMinutes, Integer resolveMinutes) {}
