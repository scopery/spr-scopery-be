package com.company.scopery.common.outbox;

/**
 * Optional validator for outbox event codes.
 * Implementations may live outside {@code common} (e.g. bootstrap) and consult EventRegistry.
 * When absent, enqueue logs and allows emission (documented Phase 04 monitoring path).
 */
@FunctionalInterface
public interface OutboxEventCodeValidator {
    boolean isKnown(String eventCode);
}
