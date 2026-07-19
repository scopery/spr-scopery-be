package com.company.scopery.modules.integrationhub.deadletter.infrastructure.persistence;

import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEvent;
import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEventRepository;
import com.company.scopery.modules.integrationhub.deadletter.infrastructure.mapper.DeadLetterEventPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaDeadLetterEventRepository implements DeadLetterEventRepository {
    private final SpringDataDeadLetterEventJpaRepository spring;
    private final DeadLetterEventPersistenceMapper mapper;

    public JpaDeadLetterEventRepository(SpringDataDeadLetterEventJpaRepository spring, DeadLetterEventPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public DeadLetterEvent save(DeadLetterEvent event) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(event)));
    }

    @Override
    public Optional<DeadLetterEvent> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<DeadLetterEvent> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
