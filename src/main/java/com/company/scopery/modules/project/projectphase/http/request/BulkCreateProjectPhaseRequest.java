package com.company.scopery.modules.project.projectphase.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateProjectPhaseRequest(
        @NotNull @Size(min = 1, max = 500)
        List<@Valid CreateProjectPhaseRequest> items
) {}
