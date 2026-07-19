package com.company.scopery.modules.configuration.fieldvalue.application.response;

import com.company.scopery.modules.configuration.fieldvalue.domain.model.CustomFieldValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Schema(description = "Custom field value details for a specific object")
public record CustomFieldValueResponse(
        @Schema(description = "Unique identifier of the field value record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Identifier of the custom field definition", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID customFieldDefinitionId,

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
        String valueOptionIds) {

    public static CustomFieldValueResponse from(CustomFieldValue v) {
        return new CustomFieldValueResponse(v.id(), v.customFieldDefinitionId(), v.valueText(), v.valueLongText(), v.valueNumber(),
                v.valueDecimal(), v.valueBoolean(), v.valueDate(), v.valueDatetime(), v.valueJson(), v.valueOptionIds());
    }
}
