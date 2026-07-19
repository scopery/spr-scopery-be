package com.company.scopery.modules.project.templatewbs.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MoveProjectTemplateWbsNodeRequest(
        UUID newParentId,
        @NotNull Integer newOrderIndex
) {}
