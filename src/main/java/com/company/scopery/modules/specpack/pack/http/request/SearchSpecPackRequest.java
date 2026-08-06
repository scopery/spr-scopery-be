package com.company.scopery.modules.specpack.pack.http.request;

public record SearchSpecPackRequest(
        String keyword,
        String packType,
        String status,
        int page,
        int size,
        String sortBy,
        boolean ascending
) {
    public SearchSpecPackRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
