package com.company.scopery.modules.traceability.functionalitem.application.command;

import com.company.scopery.modules.traceability.functionalitem.http.request.ImportFunctionalItemEntry;

import java.util.List;
import java.util.UUID;

public record PreviewFunctionalItemsImportCommand(
        UUID projectId,
        List<ImportFunctionalItemEntry> items
) {}
