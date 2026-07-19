package com.company.scopery.modules.productivity.command.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface SpringDataCommandDefinitionJpaRepository extends JpaRepository<CommandDefinitionJpaEntity, UUID> {
    List<CommandDefinitionJpaEntity> findByEnabledTrueOrderByTitleAsc();
}
