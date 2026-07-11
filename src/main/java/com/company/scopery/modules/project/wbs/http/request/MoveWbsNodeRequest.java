package com.company.scopery.modules.project.wbs.http.request;

import java.util.UUID;

public record MoveWbsNodeRequest(
        UUID newParentId,
        int newSortOrder
) {}
