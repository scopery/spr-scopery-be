package com.company.scopery.modules.knowledge.documenttype.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.knowledge.documenttype.application.response.DocumentClassificationResponse;
import com.company.scopery.modules.knowledge.documenttype.domain.enums.DocumentClassification;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Knowledge - Document Classifications", description = "Document classification vocabulary")
@RestController
@RequestMapping(KnowledgeApiPaths.DOCUMENT_CLASSIFICATIONS)
public class DocumentClassificationController {

    @Operation(summary = "List document classification values")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentClassificationResponse>>> list() {
        List<DocumentClassificationResponse> items = Arrays.stream(DocumentClassification.values())
                .filter(c -> c != DocumentClassification.CUSTOM)
                .map(c -> new DocumentClassificationResponse(c.name(), toDisplayName(c)))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    private static String toDisplayName(DocumentClassification classification) {
        return switch (classification) {
            case PUBLIC -> "Public";
            case INTERNAL -> "Internal";
            case CONFIDENTIAL -> "Confidential";
            case RESTRICTED -> "Restricted";
            case CUSTOM -> "Custom";
        };
    }
}
