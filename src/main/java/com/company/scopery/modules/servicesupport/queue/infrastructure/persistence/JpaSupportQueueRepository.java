package com.company.scopery.modules.servicesupport.queue.infrastructure.persistence;

import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueue;
import com.company.scopery.modules.servicesupport.queue.domain.model.SupportQueueRepository;
import com.company.scopery.modules.servicesupport.queue.infrastructure.mapper.SupportQueuePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSupportQueueRepository implements SupportQueueRepository {
    private final SpringDataSupportQueueJpaRepository spring;
    private final SupportQueuePersistenceMapper mapper;
    public JpaSupportQueueRepository(SpringDataSupportQueueJpaRepository spring, SupportQueuePersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SupportQueue save(SupportQueue q) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(q))); }
    @Override public Optional<SupportQueue> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportQueue> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
