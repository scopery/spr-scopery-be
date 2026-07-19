package com.company.scopery.modules.resourcecapacity.resourceprofile.http.request;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
public record CreateResourceProfileRequest(
        @NotBlank String resourceType, @NotBlank String displayName,
        UUID linkedUserId, UUID linkedWorkspaceMemberId, UUID primaryRoleId) {}
