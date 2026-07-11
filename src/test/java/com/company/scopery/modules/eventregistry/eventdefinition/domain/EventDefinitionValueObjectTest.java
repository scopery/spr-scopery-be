package com.company.scopery.modules.eventregistry.eventdefinition.domain;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EventDefinitionValueObjectTest {

    // ── EventDefinitionCode ───────────────────────────────────────────────────

    @Test
    void eventDefinitionCode_normalizesLowercaseToUppercase() {
        EventDefinitionCode code = EventDefinitionCode.of("hrm_cv_uploaded");
        assertThat(code.value()).isEqualTo("HRM_CV_UPLOADED");
    }

    @Test
    void eventDefinitionCode_acceptsAlreadyUppercase() {
        EventDefinitionCode code = EventDefinitionCode.of("CV_UPLOADED");
        assertThat(code.value()).isEqualTo("CV_UPLOADED");
    }

    @Test
    void eventDefinitionCode_rejectsInvalidCharacters() {
        assertThatThrownBy(() -> EventDefinitionCode.of("invalid-code"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uppercase letters, numbers, and underscores");
    }

    @Test
    void eventDefinitionCode_rejectsBlank() {
        assertThatThrownBy(() -> EventDefinitionCode.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void eventDefinitionCode_rejectsNull() {
        assertThatThrownBy(() -> EventDefinitionCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── SourceSystemCode ──────────────────────────────────────────────────────

    @Test
    void sourceSystemCode_normalizesLowercaseToUppercase() {
        SourceSystemCode code = SourceSystemCode.of("hrm");
        assertThat(code.value()).isEqualTo("HRM");
    }

    @Test
    void sourceSystemCode_rejectsInvalidCharacters() {
        assertThatThrownBy(() -> SourceSystemCode.of("hrm-system"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uppercase letters, numbers, and underscores");
    }

    @Test
    void sourceSystemCode_rejectsBlank() {
        assertThatThrownBy(() -> SourceSystemCode.of("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── EventKey ──────────────────────────────────────────────────────────────

    @Test
    void eventKey_normalizesLowercaseToUppercase() {
        EventKey key = EventKey.of("cv_uploaded");
        assertThat(key.value()).isEqualTo("CV_UPLOADED");
    }

    @Test
    void eventKey_rejectsInvalidCharacters() {
        assertThatThrownBy(() -> EventKey.of("cv.uploaded"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uppercase letters, numbers, and underscores");
    }

    @Test
    void eventKey_rejectsBlank() {
        assertThatThrownBy(() -> EventKey.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}