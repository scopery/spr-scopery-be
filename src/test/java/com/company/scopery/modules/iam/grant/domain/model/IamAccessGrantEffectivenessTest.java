package com.company.scopery.modules.iam.grant.domain.model;

import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IamAccessGrantEffectivenessTest {

    @Test
    void isEffectiveAt_activeWithoutExpiry_true() {
        IamAccessGrant grant = IamAccessGrant.create(
                IamSubjectType.USER, UUID.randomUUID(), UUID.randomUUID(), null,
                IamGrantEffect.ALLOW, null, null, null, UUID.randomUUID());
        assertThat(grant.isEffectiveAt(Instant.now())).isTrue();
        assertThat(grant.status()).isEqualTo(IamAccessGrantStatus.ACTIVE);
    }

    @Test
    void isEffectiveAt_expiredGrant_false() {
        Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
        IamAccessGrant grant = IamAccessGrant.createWithMetadata(
                IamSubjectType.USER, UUID.randomUUID(), UUID.randomUUID(), null,
                IamGrantEffect.ALLOW, null, null, null,
                IamGrantKind.DIRECT, null, false, 0, past, null, null, UUID.randomUUID());
        assertThat(grant.isEffectiveAt(Instant.now())).isFalse();
    }

    @Test
    void isEffectiveAt_revokedGrant_false() {
        IamAccessGrant grant = IamAccessGrant.create(
                IamSubjectType.USER, UUID.randomUUID(), UUID.randomUUID(), null,
                IamGrantEffect.ALLOW, null, null, null, UUID.randomUUID()).revoke();
        assertThat(grant.isEffectiveAt(Instant.now())).isFalse();
        assertThat(grant.status()).isEqualTo(IamAccessGrantStatus.REVOKED);
    }

    @Test
    void isEffectiveAt_futureExpiry_true() {
        Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
        IamAccessGrant grant = IamAccessGrant.createWithMetadata(
                IamSubjectType.USER, UUID.randomUUID(), UUID.randomUUID(), null,
                IamGrantEffect.ALLOW, null, null, null,
                IamGrantKind.DIRECT, null, false, 0, future, null, null, UUID.randomUUID());
        assertThat(grant.isEffectiveAt(Instant.now())).isTrue();
    }
}
