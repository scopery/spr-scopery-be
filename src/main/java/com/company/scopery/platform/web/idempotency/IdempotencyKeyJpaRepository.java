package com.company.scopery.platform.web.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;

public interface IdempotencyKeyJpaRepository extends JpaRepository<IdempotencyKeyJpaEntity, String> {
    Optional<IdempotencyKeyJpaEntity> findByKeyHashAndExpiresAtAfter(String keyHash, Instant now);
}
