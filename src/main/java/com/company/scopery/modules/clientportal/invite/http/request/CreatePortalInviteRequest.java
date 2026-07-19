package com.company.scopery.modules.clientportal.invite.http.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record CreatePortalInviteRequest(@NotBlank @Email String email, Integer expiresInDays) {}
