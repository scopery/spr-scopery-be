package com.company.scopery.modules.configuration.fieldvalue.application.command;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
public record UpsertFieldValuesCommand(UUID workspaceId, String objectType, UUID targetId, List<FieldValueEntry> values) {
    public record FieldValueEntry(UUID fieldId, String valueText, String valueLongText, Double valueNumber,
                                  BigDecimal valueDecimal, Boolean valueBoolean, LocalDate valueDate,
                                  Instant valueDatetime, String valueJson, String valueOptionIds) {}
}
