package com.company.scopery.modules.notification.advanced.channelpref.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpsertChannelPreferenceRequest(@NotBlank String categoryCode, @NotBlank String channelCode, boolean enabled) {}
