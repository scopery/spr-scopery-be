package com.company.scopery.modules.specpack.pack.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSpecPackRequest(
        @NotBlank @Size(max = 255) String name,
        String description
) {}
