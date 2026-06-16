package com.company.scopery.modules.eventregistry.shared.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class EventRegistryErrorCatalogTest {

    @Test
    void notFoundEntries_haveHttpStatus404() {
        assertThat(EventRegistryErrorCatalog.EVENT_DEFINITION_NOT_FOUND.httpStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void conflictEntries_haveHttpStatus409() {
        assertThat(EventRegistryErrorCatalog.EVENT_DEFINITION_CODE_ALREADY_EXISTS.httpStatus())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(EventRegistryErrorCatalog.EVENT_DEFINITION_SOURCE_EVENT_ALREADY_EXISTS.httpStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void businessRuleEntries_haveHttpStatus422() {
        assertThat(EventRegistryErrorCatalog.DEPRECATED_EVENT_DEFINITION_CANNOT_BE_ACTIVATED.httpStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void invalidInputEntries_haveHttpStatus400() {
        assertThat(EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_STATUS.httpStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_INPUT_SCHEMA_JSON.httpStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_OUTPUT_SCHEMA_JSON.httpStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void eachEntry_hasNonBlankCodeAndMessage() {
        for (EventRegistryErrorCatalog entry : EventRegistryErrorCatalog.values()) {
            assertThat(entry.code()).as(entry.name() + ".code").isNotBlank();
            assertThat(entry.defaultMessage()).as(entry.name() + ".defaultMessage").isNotBlank();
            assertThat(entry.httpStatus()).as(entry.name() + ".httpStatus").isNotNull();
        }
    }
}