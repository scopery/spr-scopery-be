package com.company.scopery.modules.specpack.blockimport.domain.model;

import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockImportDecision;

import java.util.List;
import java.util.Map;

public record BlockImportItem(
        String blockKey,
        String blockType,
        String title,
        String parentBlockKey,
        int displayOrder,
        String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        Map<String, Object> selfCheck,
        List<String> validationErrors,
        BlockImportDecision mergeDecision
) {
    public boolean isValid() {
        return validationErrors == null || validationErrors.isEmpty();
    }

    public BlockImportItem withDecision(BlockImportDecision decision) {
        return new BlockImportItem(blockKey, blockType, title, parentBlockKey,
                displayOrder, contentFormat, contentJson, sourceRefsJson,
                selfCheck, validationErrors, decision);
    }

    public BlockImportItem withErrors(List<String> errors) {
        return new BlockImportItem(blockKey, blockType, title, parentBlockKey,
                displayOrder, contentFormat, contentJson, sourceRefsJson,
                selfCheck, errors, mergeDecision);
    }
}
