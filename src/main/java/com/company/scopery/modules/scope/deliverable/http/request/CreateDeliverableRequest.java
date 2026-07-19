package com.company.scopery.modules.scope.deliverable.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateDeliverableRequest(@NotBlank String type, String code, @NotBlank String title, String description, Boolean acceptanceRequired) {}
