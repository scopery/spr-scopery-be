package com.company.scopery.modules.projectbaseline.changerequest.http.request;
import jakarta.validation.constraints.NotBlank;
public record RejectChangeRequestRequest(@NotBlank String reason) {}
