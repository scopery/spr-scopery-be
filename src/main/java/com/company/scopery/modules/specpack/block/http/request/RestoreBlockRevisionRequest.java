package com.company.scopery.modules.specpack.block.http.request;

import jakarta.validation.constraints.NotNull;

public record RestoreBlockRevisionRequest(
        @NotNull Integer revisionNumber
) {}
