package com.company.scopery.modules.traceability.functionalitem.http.request;

import java.util.List;

public record ImportFunctionalItemsExecuteRequest(
        List<ImportFunctionalItemEntry> toCreate,
        List<ImportFunctionalItemUpdateEntry> toUpdate,
        boolean archiveUnmatched
) {}
