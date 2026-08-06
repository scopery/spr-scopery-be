package com.company.scopery.modules.specpack.pack.application.query;

import java.util.UUID;

public record SearchSpecPackQuery(
        UUID projectId,
        String keyword,
        String packType,
        String status,
        int page,
        int size,
        String sortBy,
        boolean ascending
) {}
