package com.company.scopery.modules.project.task.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkCreateTaskRequest(
        @NotNull @Size(min = 1, max = 500) List<@Valid CreateTaskRequest> items
) {}
