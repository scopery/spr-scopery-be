package com.company.scopery.modules.servicesupport.assignment.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupportAssignmentDomainTest {
    @Test
    void assignUserAndRelease_success() {
        var a = SupportAssignment.assignUser(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertThat(a.status()).isEqualTo("ACTIVE");
        assertThat(a.release().status()).isEqualTo("RELEASED");
    }
}
