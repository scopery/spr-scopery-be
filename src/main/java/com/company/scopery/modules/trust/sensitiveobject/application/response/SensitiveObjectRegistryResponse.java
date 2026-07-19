package com.company.scopery.modules.trust.sensitiveobject.application.response;

import com.company.scopery.modules.trust.sensitiveobject.domain.model.SensitiveObjectRegistry;

import java.util.UUID;

public record SensitiveObjectRegistryResponse(
        UUID id, String objectTypeCode, String classification,
        boolean exportReasonRequired, boolean searchIndexAllowed, boolean enabled) {

    public static SensitiveObjectRegistryResponse from(SensitiveObjectRegistry e) {
        return new SensitiveObjectRegistryResponse(
                e.id(), e.objectTypeCode(), e.classification(),
                e.exportReasonRequired(), e.searchIndexAllowed(), e.enabled());
    }
}
