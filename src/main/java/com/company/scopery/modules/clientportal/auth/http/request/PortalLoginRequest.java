package com.company.scopery.modules.clientportal.auth.http.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record PortalLoginRequest(@NotNull UUID workspaceId, @NotBlank @Email String email, @NotBlank String password) {}
