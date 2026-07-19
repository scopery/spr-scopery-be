package com.company.scopery.modules.projectbaseline.changeorder.http.request;
import jakarta.validation.constraints.NotBlank;
public record RejectChangeOrderRequest(@NotBlank String reason) {}
