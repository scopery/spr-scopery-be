package com.company.scopery.modules.iam.me.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EffectivePermissionsResponse(
        List<String> permissions,
        List<String> roles,
        @JsonProperty("org_role") String orgRole,
        @JsonProperty("project_role") String projectRole) {

    public static EffectivePermissionsResponse of(List<String> permissions) {
        return new EffectivePermissionsResponse(permissions, List.of(), null, null);
    }
}
