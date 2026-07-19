package com.company.scopery.modules.aiplanning.suggestionitem.domain.model;

import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemOperation;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiPlanningSuggestionItemDomainTest {

    private final UUID actorId = UUID.randomUUID();

    private AiPlanningSuggestionItem proposed(SuggestionItemType type) {
        return AiPlanningSuggestionItem.create(
                UUID.randomUUID(), UUID.randomUUID(), type, SuggestionItemOperation.CREATE,
                "Item", "Desc", "{\"hours\":8}", "Why", "MED", "TASK", null);
    }

    @Test
    void acceptItem_valid_success() {
        assertThat(proposed(SuggestionItemType.TASK).accept(actorId).status())
                .isEqualTo(SuggestionItemStatus.ACCEPTED);
    }

    @Test
    void rejectItem_valid_success() {
        assertThat(proposed(SuggestionItemType.TASK).reject(actorId).status())
                .isEqualTo(SuggestionItemStatus.REJECTED);
    }

    @Test
    void markApplied_requiresAccepted() {
        assertThatThrownBy(() -> proposed(SuggestionItemType.TASK).markApplied(actorId, "CREATE", "{}"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void markApplied_fromAccepted_success() {
        AiPlanningSuggestionItem applied = proposed(SuggestionItemType.FINANCE_NOTE)
                .accept(actorId)
                .markApplied(actorId, "RECORD_PROPOSAL", "{}");
        assertThat(applied.status()).isEqualTo(SuggestionItemStatus.APPLIED);
        assertThat(applied.applyAction()).isEqualTo("RECORD_PROPOSAL");
    }

    @Test
    void markSkipped_fromAccepted() {
        AiPlanningSuggestionItem skipped = proposed(SuggestionItemType.WBS_NODE)
                .accept(actorId)
                .markSkipped("{\"reason\":\"CR\"}");
        assertThat(skipped.status()).isEqualTo(SuggestionItemStatus.SKIPPED);
    }
}
