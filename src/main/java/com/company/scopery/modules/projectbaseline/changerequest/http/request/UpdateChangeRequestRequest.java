package com.company.scopery.modules.projectbaseline.changerequest.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record UpdateChangeRequestRequest(
        @NotBlank String title, String description, @NotBlank String changeType,
        String priority, UUID baselineId, String reason) {}
