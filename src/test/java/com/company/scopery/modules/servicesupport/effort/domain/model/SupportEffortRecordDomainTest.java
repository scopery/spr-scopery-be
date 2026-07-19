package com.company.scopery.modules.servicesupport.effort.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SupportEffortRecordDomainTest {

    @Test
    void create_andCancel() {
        var e = SupportEffortRecord.create(UUID.randomUUID(), UUID.randomUUID(), null,
                new BigDecimal("2.5"), LocalDate.now());
        assertEquals("RECORDED", e.status());
        assertEquals("CANCELLED", e.cancel().status());
    }

    @Test
    void rejectsNonPositiveHours() {
        assertThrows(IllegalArgumentException.class, () ->
                SupportEffortRecord.create(UUID.randomUUID(), UUID.randomUUID(), null, BigDecimal.ZERO, null));
    }
}
