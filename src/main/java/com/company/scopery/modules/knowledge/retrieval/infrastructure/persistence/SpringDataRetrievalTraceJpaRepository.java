package com.company.scopery.modules.knowledge.retrieval.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataRetrievalTraceJpaRepository extends JpaRepository<RetrievalTraceJpaEntity, UUID> {
}
