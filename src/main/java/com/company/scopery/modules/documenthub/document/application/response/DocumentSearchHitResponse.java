package com.company.scopery.modules.documenthub.document.application.response;

import com.company.scopery.modules.documenthub.document.domain.model.Document;

import java.util.UUID;

public record DocumentSearchHitResponse(
        UUID id,
        String code,
        String title,
        String status,
        String maskedDescriptionSnippet) {

    public static DocumentSearchHitResponse from(Document d, String maskedDescription) {
        String snippet = maskedDescription == null ? "" : maskedDescription;
        if (snippet.length() > 200) {
            snippet = snippet.substring(0, 200);
        }
        return new DocumentSearchHitResponse(
                d.id(), d.code(), d.title(), d.status().name(), snippet);
    }
}
