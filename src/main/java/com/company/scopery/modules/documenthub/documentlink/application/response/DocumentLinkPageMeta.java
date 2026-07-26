package com.company.scopery.modules.documenthub.documentlink.application.response;

public record DocumentLinkPageMeta(
        int limit,
        int offset,
        long total
) {}
