package com.company.scopery.modules.specpack.blockimport.application.response;

import com.company.scopery.modules.specpack.blockimport.domain.model.BlockImportItem;
import com.company.scopery.modules.specpack.blockimport.domain.model.ImportPreview;

import java.util.List;

public record ImportPreviewResponse(
        String schemaVersion,
        int totalItems,
        int validItems,
        int invalidItems,
        int createCount,
        int replaceCount,
        int skipCount,
        boolean hasErrors,
        List<BlockImportItemResponse> items
) {
    public static ImportPreviewResponse from(ImportPreview preview) {
        return new ImportPreviewResponse(
                preview.schemaVersion(),
                preview.totalItems(),
                preview.validItems(),
                preview.invalidItems(),
                preview.createCount(),
                preview.replaceCount(),
                preview.skipCount(),
                preview.hasErrors(),
                preview.items().stream().map(BlockImportItemResponse::from).toList()
        );
    }

    public record BlockImportItemResponse(
            String blockKey,
            String blockType,
            String title,
            String parentBlockKey,
            int displayOrder,
            String contentFormat,
            List<String> validationErrors,
            String mergeDecision
    ) {
        public static BlockImportItemResponse from(BlockImportItem item) {
            return new BlockImportItemResponse(
                    item.blockKey(), item.blockType(), item.title(),
                    item.parentBlockKey(), item.displayOrder(), item.contentFormat(),
                    item.validationErrors(),
                    item.mergeDecision() != null ? item.mergeDecision().name() : null
            );
        }
    }
}
