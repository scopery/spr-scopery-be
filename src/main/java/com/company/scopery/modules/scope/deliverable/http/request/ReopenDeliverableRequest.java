package com.company.scopery.modules.scope.deliverable.http.request;
import jakarta.validation.constraints.NotBlank;
public record ReopenDeliverableRequest(@NotBlank String reason) {}
