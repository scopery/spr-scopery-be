package com.company.scopery.modules.aiagent.capability.domain;

import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterCapabilityStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterIfNullBehavior;
import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterIfNullBehavior.*;

class ModelParameterCapabilityTest {

    private static final UUID MODEL_ID = UUID.randomUUID();
    private static final ModelParameterName TEMPERATURE = ModelParameterName.of("TEMPERATURE");

    @Test
    void create_validNumberParameter_succeeds() {
        ModelParameterCapability cap = ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "temperature",
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                BigDecimal.ZERO, new BigDecimal("2.0"),
                null, true, DO_NOT_SEND_PARAMETER, null);

        assertThat(cap.id()).isNotNull();
        assertThat(cap.status()).isEqualTo(ModelParameterCapabilityStatus.ACTIVE);
        assertThat(cap.parameterName().value()).isEqualTo("TEMPERATURE");
        assertThat(cap.minValue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(cap.maxValue()).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void create_minGreaterThanMax_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "temperature",
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                new BigDecimal("3.0"), new BigDecimal("2.0"),
                null, false, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minValue must be less than or equal to maxValue");
    }

    @Test
    void create_booleanWithMinValue_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterCapability.create(
                MODEL_ID, ModelParameterName.of("JSON_OUTPUT"), "json_mode",
                ModelParameterSupportStatus.YES, ModelParameterValueType.BOOLEAN,
                BigDecimal.ZERO, null, null, false, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BOOLEAN");
    }

    @Test
    void create_stringWithMaxValue_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterCapability.create(
                MODEL_ID, ModelParameterName.of("IF_NULL_BEHAVIOR"), "if_null_behavior",
                ModelParameterSupportStatus.YES, ModelParameterValueType.STRING,
                null, new BigDecimal("100"), null, false, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("STRING");
    }

    @Test
    void create_nullableWithoutIfNullBehavior_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "temperature",
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                null, null, null, true, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ifNullBehavior is required when nullable is true");
    }

    @Test
    void create_supportStatusYesWithoutApiKey_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, null,
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("API parameter key is required");
    }

    @Test
    void create_supportStatusNoWithoutApiKey_succeeds() {
        ModelParameterCapability cap = ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, null,
                ModelParameterSupportStatus.NO, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null);

        assertThat(cap.supportStatus()).isEqualTo(ModelParameterSupportStatus.NO);
        assertThat(cap.apiParameterKey()).isNull();
    }

    @Test
    void create_conditionalWithoutApiKey_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "",
                ModelParameterSupportStatus.CONDITIONAL, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("API parameter key is required");
    }

    @Test
    void activate_setsStatusToActive() {
        ModelParameterCapability cap = ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "temperature",
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null);
        cap.deactivate();
        assertThat(cap.status()).isEqualTo(ModelParameterCapabilityStatus.INACTIVE);

        cap.activate();
        assertThat(cap.status()).isEqualTo(ModelParameterCapabilityStatus.ACTIVE);
    }

    @Test
    void deactivate_setsStatusToInactive() {
        ModelParameterCapability cap = ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "temperature",
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null);

        cap.deactivate();
        assertThat(cap.status()).isEqualTo(ModelParameterCapabilityStatus.INACTIVE);
    }

    @Test
    void update_changeSupportStatusToNo_clearsApiKeyRequirement() {
        ModelParameterCapability cap = ModelParameterCapability.create(
                MODEL_ID, TEMPERATURE, "temperature",
                ModelParameterSupportStatus.YES, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null);

        cap.update(null, ModelParameterSupportStatus.NO, ModelParameterValueType.NUMBER,
                null, null, null, false, null, null);

        assertThat(cap.supportStatus()).isEqualTo(ModelParameterSupportStatus.NO);
        assertThat(cap.apiParameterKey()).isNull();
    }
}
