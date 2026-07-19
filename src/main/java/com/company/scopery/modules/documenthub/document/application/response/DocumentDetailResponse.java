package com.company.scopery.modules.documenthub.document.application.response;

import com.company.scopery.modules.documenthub.document.domain.model.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DocumentDetailResponse(
        UUID id, UUID projectId, String code, String title, String status,
        String description, UUID currentVersionId, Instant createdAt,
        Map<String, Object> maskedFields) {

    public static DocumentDetailResponse from(Document d, Map<String, Object> maskedFields) {
        return new DocumentDetailResponse(
                d.id(), d.projectId(), d.code(), d.title(), d.status().name(),
                d.description(), d.currentVersionId(), d.createdAt(), maskedFields);
    }
}
