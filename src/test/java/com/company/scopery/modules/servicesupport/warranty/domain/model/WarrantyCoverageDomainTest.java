package com.company.scopery.modules.servicesupport.warranty.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WarrantyCoverageDomainTest {

    @Test
    void covers_activeWindow() {
        var w = WarrantyCoverage.create(UUID.randomUUID(), UUID.randomUUID(), null,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        assertTrue(w.covers(LocalDate.of(2026, 6, 1)));
        assertFalse(w.covers(LocalDate.of(2025, 12, 31)));
        assertEquals("EXPIRED", w.expire().status());
    }
}
