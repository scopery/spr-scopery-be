package com.company.scopery.modules.servicesupport.handover.http.request;
import jakarta.validation.constraints.NotBlank;
public record AddHandoverItemRequest(@NotBlank String itemType, @NotBlank String title) {}
