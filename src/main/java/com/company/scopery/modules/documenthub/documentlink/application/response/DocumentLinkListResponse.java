package com.company.scopery.modules.documenthub.documentlink.application.response;

import java.util.List;

public record DocumentLinkListResponse(
        List<DocumentLinkResponse> items,
        DocumentLinkPageMeta page
) {}
