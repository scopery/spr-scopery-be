package com.company.scopery.modules.resourcereference.resourcetype.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataMentionResourceTypeJpaRepository extends JpaRepository<MentionResourceTypeJpaEntity, UUID> {
    Optional<MentionResourceTypeJpaEntity> findByCode(String code);
    boolean existsByCode(String code);
    List<MentionResourceTypeJpaEntity> findByEnabledTrueOrderByCodeAsc();
    List<MentionResourceTypeJpaEntity> findAllByOrderByCodeAsc();
}
