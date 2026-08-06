package com.company.scopery.modules.specpack.blockimport.domain.model;

import java.util.List;

public record ImportPreview(
        String schemaVersion,
        int totalItems,
        int validItems,
        int invalidItems,
        int createCount,
        int replaceCount,
        int skipCount,
        List<BlockImportItem> items
) {
    public boolean hasErrors() {
        return invalidItems > 0;
    }
}
