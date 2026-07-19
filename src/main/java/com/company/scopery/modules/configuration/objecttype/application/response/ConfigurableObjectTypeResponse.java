package com.company.scopery.modules.configuration.objecttype.application.response;

import com.company.scopery.modules.configuration.objecttype.infrastructure.persistence.ConfigurableObjectTypeJpaEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Configurable object type details describing which features are enabled for an entity type")
public record ConfigurableObjectTypeResponse(
        @Schema(description = "Unique identifier of the object type", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique code identifying the object type", example = "PROJECT")
        String code,

        @Schema(description = "Display name of the object type", example = "Project")
        String name,

        @Schema(description = "Whether custom fields are enabled for this object type", example = "true")
        boolean customFieldsEnabled,

        @Schema(description = "Whether forms are enabled for this object type", example = "true")
        boolean formsEnabled,

        @Schema(description = "Whether tags are enabled for this object type", example = "true")
        boolean tagsEnabled,

        @Schema(description = "Whether custom status sets are enabled for this object type", example = "false")
        boolean customStatusEnabled,

        @Schema(description = "Whether this object type is active and available in the system", example = "true")
        boolean enabled) {

    public static ConfigurableObjectTypeResponse from(ConfigurableObjectTypeJpaEntity e) {
        return new ConfigurableObjectTypeResponse(e.getId(), e.getCode(), e.getName(), e.isCustomFieldsEnabled(), e.isFormsEnabled(),
                e.isTagsEnabled(), e.isCustomStatusEnabled(), e.isEnabled());
    }
}
