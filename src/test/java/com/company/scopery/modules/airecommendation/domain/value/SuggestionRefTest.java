package com.company.scopery.modules.airecommendation.domain.value;

import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SuggestionRefTest {

    private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void parse_p43Prefix_returnsPhase43() {
        SuggestionRef ref = SuggestionRef.parse("p43:" + id);
        assertThat(ref.sourceSystem()).isEqualTo(SourceSystem.PHASE43);
        assertThat(ref.uuid()).isEqualTo(id);
        assertThat(ref.isPhase43()).isTrue();
        assertThat(ref.isPhase21()).isFalse();
    }

    @Test
    void parse_p21Prefix_returnsPhase21() {
        SuggestionRef ref = SuggestionRef.parse("p21:" + id);
        assertThat(ref.sourceSystem()).isEqualTo(SourceSystem.PHASE21);
        assertThat(ref.uuid()).isEqualTo(id);
        assertThat(ref.isPhase21()).isTrue();
        assertThat(ref.isPhase43()).isFalse();
    }

    @Test
    void parse_invalidPrefix_throws() {
        assertThatThrownBy(() -> SuggestionRef.parse("p42:" + id))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void parse_noPrefix_throws() {
        assertThatThrownBy(() -> SuggestionRef.parse(id.toString()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void parse_null_throws() {
        assertThatThrownBy(() -> SuggestionRef.parse(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void parse_blank_throws() {
        assertThatThrownBy(() -> SuggestionRef.parse("   "))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void parse_invalidUuid_throws() {
        assertThatThrownBy(() -> SuggestionRef.parse("p43:not-a-uuid"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void toString_p43_producesCorrectFormat() {
        SuggestionRef ref = SuggestionRef.ofPhase43(id);
        assertThat(ref.toString()).isEqualTo("p43:" + id);
    }

    @Test
    void toString_p21_producesCorrectFormat() {
        SuggestionRef ref = SuggestionRef.ofPhase21(id);
        assertThat(ref.toString()).isEqualTo("p21:" + id);
    }

    @Test
    void parseRoundTrip_preservesValue() {
        SuggestionRef original = SuggestionRef.ofPhase43(id);
        SuggestionRef parsed = SuggestionRef.parse(original.toString());
        assertThat(parsed).isEqualTo(original);
    }
}
