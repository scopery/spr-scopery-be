package com.company.scopery.modules.eventregistry.eventdefinition.domain;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventVariablePath;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EventDefinitionValueObjectTest {

    @Test
    void eventDefinitionCode_normalizesLowercaseToUppercase() {
        EventDefinitionCode code = EventDefinitionCode.of("hrm_cv_uploaded");
        assertThat(code.value()).isEqualTo("HRM_CV_UPLOADED");
    }

    @Test
    void eventDefinitionCode_rejectsInvalidCharacters() {
        assertThatThrownBy(() -> EventDefinitionCode.of("invalid-code"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("^[A-Z][A-Z0-9_]{2,149}$");
    }

    @Test
    void eventDefinitionCode_rejectsTooShort() {
        assertThatThrownBy(() -> EventDefinitionCode.of("AB"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eventDefinitionCode_rejectsBlank() {
        assertThatThrownBy(() -> EventDefinitionCode.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void sourceSystemCode_normalizesLowercaseToUppercase() {
        SourceSystemCode code = SourceSystemCode.of("hrm");
        assertThat(code.value()).isEqualTo("HRM");
    }

    @Test
    void eventKey_normalizesUpperSnake() {
        EventKey key = EventKey.of("cv_uploaded");
        assertThat(key.value()).isEqualTo("CV_UPLOADED");
    }

    @Test
    void eventKey_acceptsLowercaseDotStyle() {
        EventKey key = EventKey.of("task.assigned");
        assertThat(key.value()).isEqualTo("task.assigned");
    }

    @Test
    void eventKey_rejectsInvalidCharacters() {
        assertThatThrownBy(() -> EventKey.of("task assigned"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eventVariablePath_acceptsDotNotation() {
        assertThat(EventVariablePath.of("actor.userId").value()).isEqualTo("actor.userId");
    }

    @Test
    void eventVariablePath_rejectsLeadingDot() {
        assertThatThrownBy(() -> EventVariablePath.of(".actor"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
