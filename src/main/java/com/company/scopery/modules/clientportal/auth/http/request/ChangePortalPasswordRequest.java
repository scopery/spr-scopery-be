package com.company.scopery.modules.clientportal.auth.http.request;
import jakarta.validation.constraints.NotBlank;
public record ChangePortalPasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
