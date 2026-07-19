package com.company.scopery.modules.resourcereference.batchresolve.http.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record BatchResolveRequest(
        @NotEmpty @Size(max = 200) List<RefItem> refs
) {
    public record RefItem(@NotNull String resourceType, @NotNull UUID resourceId) {}
}
