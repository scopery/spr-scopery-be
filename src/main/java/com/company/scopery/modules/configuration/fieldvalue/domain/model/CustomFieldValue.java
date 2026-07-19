package com.company.scopery.modules.configuration.fieldvalue.domain.model;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
public record CustomFieldValue(UUID id, UUID workspaceId, String objectTypeCode, UUID targetId, UUID customFieldDefinitionId,
        String valueText, String valueLongText, Double valueNumber, BigDecimal valueDecimal, Boolean valueBoolean,
        LocalDate valueDate, Instant valueDatetime, String valueJson, String valueOptionIds,
        int version, Instant createdAt, Instant updatedAt) {
    public static CustomFieldValue upsert(UUID id, UUID workspaceId, String objectType, UUID targetId, UUID fieldId,
            String text, String longText, Double number, BigDecimal decimal, Boolean bool, LocalDate date, Instant datetime,
            String json, String optionIds, Instant createdAt) {
        Instant now = Instant.now();
        return new CustomFieldValue(id != null ? id : UUID.randomUUID(), workspaceId, objectType, targetId, fieldId,
                text, longText, number, decimal, bool, date, datetime, json, optionIds, 0, createdAt != null ? createdAt : now, now);
    }
}
