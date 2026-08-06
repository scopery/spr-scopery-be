package com.company.scopery.modules.specpack.pack.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSpecPackRequest(
        @NotBlank @Size(max = 50) String packType,
        @NotBlank @Size(max = 255) String name,
        String description,
        UUID sourcePackId
) {}
