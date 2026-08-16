package com.company.scopery.modules.traceability.appcomponent.http.request;
import jakarta.validation.constraints.NotBlank;
public record ConfirmComponentScreenshotUploadRequest(@NotBlank String objectKey) {}
