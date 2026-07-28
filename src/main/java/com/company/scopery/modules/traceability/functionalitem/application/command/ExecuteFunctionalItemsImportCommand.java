package com.company.scopery.modules.traceability.functionalitem.application.command;

import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemEntry;
import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemUpdateEntry;

import java.util.List;
import java.util.UUID;

public record ExecuteFunctionalItemsImportCommand(
        UUID projectId,
        List<ImportFunctionalItemEntry> toCreate,
        List<ImportFunctionalItemUpdateEntry> toUpdate,
        boolean archiveUnmatched
) {}
