package com.company.scopery.modules.aiagent.eventconfig.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EventConfigCodeTest {

    @Test
    void of_normalizesLowercaseToUppercase() {
        EventConfigCode code = EventConfigCode.of("hrm_cv_uploaded_config");
        assertThat(code.value()).isEqualTo("HRM_CV_UPLOADED_CONFIG");
    }

    @Test
    void of_acceptsAlreadyUppercase() {
        EventConfigCode code = EventConfigCode.of("CV_UPLOAD_CONFIG");
        assertThat(code.value()).isEqualTo("CV_UPLOAD_CONFIG");
    }

    @Test
    void of_rejectsInvalidCharacters() {
        assertThatThrownBy(() -> EventConfigCode.of("invalid-code"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uppercase letters, numbers, and underscores");
    }

    @Test
    void of_rejectsBlank() {
        assertThatThrownBy(() -> EventConfigCode.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void of_rejectsNull() {
        assertThatThrownBy(() -> EventConfigCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equals_andHashCode_basedOnValue() {
        EventConfigCode a = EventConfigCode.of("TEST_CODE");
        EventConfigCode b = EventConfigCode.of("test_code");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}