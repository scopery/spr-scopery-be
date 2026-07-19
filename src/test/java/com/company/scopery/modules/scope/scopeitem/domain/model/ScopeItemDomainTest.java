package com.company.scopery.modules.scope.scopeitem.domain.model;

import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScopeItemDomainTest {

    @Test
    void create_rejectsBothInAndOutOfScope() {
        assertThatThrownBy(() -> ScopeItem.create(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), ScopeItemType.FEATURE,
                "SI-1", "Title", null, true, true, null, true, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
