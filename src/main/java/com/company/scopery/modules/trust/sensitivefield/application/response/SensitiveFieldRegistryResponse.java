package com.company.scopery.modules.trust.sensitivefield.application.response;

import com.company.scopery.modules.trust.sensitivefield.domain.model.SensitiveFieldRegistry;

import java.util.UUID;

public record SensitiveFieldRegistryResponse(
        UUID id, String objectTypeCode, String fieldPath, String classification,
        String maskingStrategy, boolean exportAllowed, boolean enabled) {

    public static SensitiveFieldRegistryResponse from(SensitiveFieldRegistry e) {
        return new SensitiveFieldRegistryResponse(
                e.id(), e.objectTypeCode(), e.fieldPath(), e.classification(),
                e.maskingStrategy(), e.exportAllowed(), e.enabled());
    }
}
