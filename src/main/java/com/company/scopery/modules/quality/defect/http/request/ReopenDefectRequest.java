package com.company.scopery.modules.quality.defect.http.request;
import jakarta.validation.constraints.NotBlank;
public record ReopenDefectRequest(@NotBlank String reason) {}
