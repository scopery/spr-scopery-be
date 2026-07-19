package com.company.scopery.modules.clientportal.auth.http.request;
import jakarta.validation.constraints.NotBlank;
public record AcceptPortalInviteRequest(@NotBlank String inviteToken, @NotBlank String password, String displayName) {}
