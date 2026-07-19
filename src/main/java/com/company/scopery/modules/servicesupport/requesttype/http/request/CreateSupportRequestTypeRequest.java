package com.company.scopery.modules.servicesupport.requesttype.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateSupportRequestTypeRequest(@NotBlank String typeCode, @NotBlank String name,
        String defaultPriority, Boolean portalVisible) {}
