package com.company.scopery.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionalOutboxJpaRepository extends JpaRepository<TransactionalOutboxJpaEntity, UUID> {
}
