package com.company.scopery.modules.traceability.screen.http.request;
import jakarta.validation.constraints.NotBlank;
public record ConfirmScreenMockupUploadRequest(@NotBlank String objectKey) {}
