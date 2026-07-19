package com.company.scopery.modules.configuration.fieldvalue.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request payload to upsert (create or update) custom field values for an object")
public record UpsertFieldValuesRequest(
        @Schema(description = "Object type code identifying the type of entity being updated", example = "PROJECT")
        @NotBlank String objectType,

        @Schema(description = "Identifier of the target object to set field values on", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID targetId,

        @Schema(description = "List of field value items to upsert")
        @NotNull List<FieldValueItem> values) {

    @Schema(description = "A single field value entry in the upsert request")
    public record FieldValueItem(
            @Schema(description = "Identifier of the custom field definition", example = "550e8400-e29b-41d4-a716-446655440001")
            @NotNull UUID fieldId,

            @Schema(description = "Text value (for TEXT field type)", example = "Some text value", nullable = true)
            String valueText,

            @Schema(description = "Long text value (for LONG_TEXT field type)", example = "A longer description text value", nullable = true)
            String valueLongText,

            @Schema(description = "Number value (for NUMBER field type)", example = "42.0", nullable = true)
            Double valueNumber,

            @Schema(description = "Decimal value (for DECIMAL or CURRENCY field type)", example = "99.99", nullable = true)
            BigDecimal valueDecimal,

            @Schema(description = "Boolean value (for BOOLEAN field type)", example = "true", nullable = true)
            Boolean valueBoolean,

            @Schema(description = "Date value (for DATE field type)", example = "2026-07-17", nullable = true)
            LocalDate valueDate,

            @Schema(description = "Datetime value (for DATETIME field type)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
            Instant valueDatetime,

            @Schema(description = "JSON value for complex types (USER, TEAM, etc.)", example = "{\"id\":\"550e8400-e29b-41d4-a716-446655440000\"}", nullable = true)
            String valueJson,

            @Schema(description = "Comma-separated option IDs for SELECT or MULTI_SELECT field types", example = "550e8400-e29b-41d4-a716-446655440000,550e8400-e29b-41d4-a716-446655440001", nullable = true)
            String valueOptionIds) {}
}
