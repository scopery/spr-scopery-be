package com.company.scopery.platform.web.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface IdempotencyKeyJpaRepository extends JpaRepository<IdempotencyKeyJpaEntity, String> {

    Optional<IdempotencyKeyJpaEntity> findByKeyHashAndExpiresAtAfter(String keyHash, Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM IdempotencyKeyJpaEntity e WHERE e.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
