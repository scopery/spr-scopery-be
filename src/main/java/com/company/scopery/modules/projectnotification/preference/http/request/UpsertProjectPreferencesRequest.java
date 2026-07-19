package com.company.scopery.modules.projectnotification.preference.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpsertProjectPreferencesRequest(
        @NotEmpty @Valid List<PreferenceItemRequest> preferences
) {
    public record PreferenceItemRequest(
            String eventCode,
            @NotNull String channel,
            boolean enabled,
            boolean muted
    ) {}
}
